package uebung_4;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Created by Mikel on 20.12.2015.
 */
public class ServerThread extends Thread {

    private static final int MAX_CONNECTIONS = 5;

    private Server server;
    private Socket socket = null;
    private boolean isLoggedIn;
    private boolean alive;

    private PrintWriter toClient;
    private BufferedReader fromClient;

    private int sequence;

    public ServerThread(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        sequence = new Random().nextInt(1000);
        isLoggedIn = false;
        alive = true;
        fromClient = new BufferedReader(
                new InputStreamReader(this.socket.getInputStream()));
        toClient = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        String input;

        toClient.println(encode("Willkommen, Anmelden mit: \"login <username>\".", StatusCode.OK));

        try {
            while (alive) {
                input = fromClient.readLine();
                if (input != null) {
                    toClient.println(process(input).toString());
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject process(String in) throws ParseException {
        // decode
        System.out.println("Input:  " + in);
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(in);
        String command = (String) obj.get("command");
        Object[] paramsObj = ((JSONArray) obj.get("params")).toArray();
        String[] params = Arrays.copyOf(paramsObj, paramsObj.length, String[].class);
        sequence = ((Long) obj.get("sequence")).intValue() + 1;

        JSONObject output = encode("", StatusCode.OK_NO_RESPONSE);

        if (!isLoggedIn) command = "login";

        if (command == null) return output;

        switch (command) {
            case "login":
                output = login(params);
                break;
            case "exit":
                output = exit(params);
                break;
            case "msg":
                output = msg(params);
                break;
            case "time":
                output = time(params);
                break;
            case "who":
                output = who(params);
                break;
            case "ls":
                output = ls(params);
                break;
            default:
                output = encode("Kommando nicht vorhanden!", StatusCode.NOT_IMPLEMENTED);
        }

        System.out.println("Output: " + output);
        return output;
    }

    private JSONObject login(String[] params) {
        if (server.clients.size() >= MAX_CONNECTIONS) {
            return encode("Maximale Anzahl an Teilnehmern erreicht!", StatusCode.SERVICE_UNAVAILABLE);
        }

        if (isLoggedIn) {
            return encode("Sie sind bereits eingeloggt!", StatusCode.UNAUTHORIZED);
        }

        if (params.length != 1) {
            return encode("usage: login <name>", StatusCode.INCORRECT_REQUEST);
        }

        for (ServerThread thread : server.clients) {
            if (thread.getName().equals(params[0])) {
                return encode("Benutzer bereits angemeldet!", StatusCode.UNAUTHORIZED);
            }
        }

        this.setName(params[0]);
        server.clients.add(this);
        isLoggedIn = true;
        return encode("Hi " + params[0] + ".", StatusCode.OK);
    }

    private JSONObject msg(String[] params) {
        if (params.length != 2) {
            return encode("usage: msg <client> \"<message>\"", StatusCode.INCORRECT_REQUEST);
        }

        ServerThread receiver = null;
        for (ServerThread thread : server.clients) {
            if (thread.getName().equals(params[0])) {
                receiver = thread;
            }
        }

        if (receiver == null) return encode("Empfänger nicht gefunden!", StatusCode.RESOURCE_NOT_FOUND);

        receiver.sendMsg(this.getName() + ": " + params[1]);

        return encode("", StatusCode.OK_NO_RESPONSE);
    }

    private JSONObject exit(String[] params) {
        if (params.length != 0) {
            return encode("usage: exit", StatusCode.INCORRECT_REQUEST);
        }
        alive = false;
        server.clients.remove(this);
        return encode("Bye.", StatusCode.OK);
    }

    private JSONObject time(String[] params) {
        if (params.length != 0) {
            return encode("usage: time", StatusCode.INCORRECT_REQUEST);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return encode(sdf.format(new Date()), StatusCode.OK);
    }

    private JSONObject who(String[] params) {
        if (params.length != 0) {
            return encode("usage: who", StatusCode.INCORRECT_REQUEST);
        }

        String[] clients = new String[server.clients.size()];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = server.clients.get(i).getName();
        }

        return encode(clients, StatusCode.OK);
    }

    private JSONObject ls(String[] params) {
        if (params.length != 1) {
            return encode("usage: ls <path>", StatusCode.INCORRECT_REQUEST);
        }

        try {
            File[] files = new File(params[0].replace("\\", "")).listFiles();
            String[] output = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                output[i] = files[i].getName();
            }
            return encode(output, StatusCode.OK);
        } catch (NullPointerException e) {
            return encode("Pfad nicht gefunden!", StatusCode.RESOURCE_NOT_FOUND);
        }
    }

    public void sendMsg(String msg) {
        toClient.println(encode(msg, StatusCode.OK).toString());
    }

    private JSONObject encode(String[] responses, StatusCode status) {
        JSONObject obj = new JSONObject();
        JSONArray resArr = new JSONArray();

        for (String response : responses) {
            resArr.add(response);
        }

        obj.put("statuscode", status.getCode());
        obj.put("response", resArr);
        obj.put("sequence", sequence);

        return obj;
    }

    private JSONObject encode(String response, StatusCode code) {
        return encode(new String[] { response }, code);
    }
}