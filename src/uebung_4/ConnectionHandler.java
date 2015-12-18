/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uebung_4;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.GregorianCalendar;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author RonNegi
 */
public class ConnectionHandler implements Runnable {

    private Client clientInstance;
    private Socket client;
    private BufferedReader reader;
    private boolean loggedIn = true;
    private PrintWriter output;
    private JSONParser jps;
    private int sequence = -1;
    private String nick;

    public ConnectionHandler(Socket client) {
        try {
            this.client = client;
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream());
            jps = new JSONParser();
        } catch (IOException ex) {
        }
    }

    @Override
    public void run() {
        while (loggedIn) {
            try {
                standByForMessages();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Waits for Incoming Messages
     *
     * @throws IOException
     */
    private void standByForMessages() throws IOException {
        if (reader.readLine() != null) {
            try {
                JSONObject j = (JSONObject) jps.parse(reader.readLine());
                sequence = Integer.valueOf(j.get("sequence").toString());
                String cmd = (String) j.get("command");
                JSONArray parameter = (JSONArray) j.get("parameter");
                cmdHandler(parameter, cmd);
            } catch (ParseException ex) {
            }
        }
    }

    /**
     * Command Handler for Incoming Commands
     *
     * @param parameter
     * @param cmd
     */
    private void cmdHandler(JSONArray parameter, String cmd) {
        switch (cmd) {
            case "login":
                signIn(parameter);
                break;
            case "time":
                Date d = new GregorianCalendar().getTime();
                clientResponse(d.toString());
                break;
            case "ls":
                listFiles(parameter);
                break;
            case "who":
                clientResponse(MailboxServer.getUsersAsString());
                break;
            case "msg":
                sendMessage(parameter);
                break;
            case "exit":
                signOut();
                break;
        }
    }

    /**
     * register User to the Server
     *
     * @param parameter
     */
    private void signIn(JSONArray parameter) {
        if (parameter.size() == 1) {
            nick = parameter.get(0).toString();
            clientInstance = new Client(client, this, nick);
            if (MailboxServer.addClientToList(nick, clientInstance) == true) {
                clientResponse("User " + nick + " connected");
            }
        } else {
            failedClientResponse("Wrong Parameters");
        }
    }

    /**
     * Log out and removal from Server
     */
    private void signOut() {
        MailboxServer.removeClient(this);
        clientResponse("GoodyBye!");
    }

    /**
     * Failure Response Code: 400
     */
    private void failedClientResponse(String error) {
        JSONObject j = new JSONObject();
        j.put("statuscode", 400);
        j.put("sequence", ++sequence);
        j.put("response", new JSONArray().add(error));
        output.println(j);
        output.flush();
    }

    /**
     * Client Response Code : 200 OK
     *
     * @param msg
     */
    private void clientResponse(String msg) {
        JSONObject j = new JSONObject();
        j.put("statuscode", 200);
        j.put("sequence", ++sequence);
        j.put("response", new JSONArray().add(msg));
        output.println(j);
        output.flush();
    }

    /**
     * Send Message to another Client
     *
     * @param msg
     */
    private void sendMessage(JSONArray parameter) {
        String msg = "";
        if (parameter.size() > 1) {
            for (int i = 1; i < parameter.size(); i++) {
                msg = msg + parameter.get(i).toString();
            }
            Client xClient = MailboxServer.getClientX((String) parameter.get(0));
            JSONObject j = new JSONObject();

            j.put("sequence", ++sequence);
            j.put("response", msg);

            if (xClient != null) {
                try {
                    PrintWriter xOutput = new PrintWriter(xClient.getClientSocket().getOutputStream());
                    xOutput.println(j);
                    xOutput.flush();
                } catch (IOException ex) {
                }
            } else {
                failedClientResponse("Client doesent exist!");
            }
            clientResponse("Message sent! 200- OK");
        }
    }

    /**
     * List All Files and Directories from a given path
     *
     * @param parameter
     */
    private void listFiles(JSONArray parameter) {
        String response = "Files and Directorys to list: ";
        if (parameter.size() == 1) {
            File file = new File((String) parameter.get(0));
            for (String s : file.list()) {
                response = response + "\n" + s;
            }
            clientResponse(response);
        } else {
            failedClientResponse("Wrong Parameters! -400");
        }
    }
}
