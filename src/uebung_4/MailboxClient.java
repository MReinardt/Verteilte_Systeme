/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uebung_4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author RonNegi
 */
public class MailboxClient {

    private Socket sock;
    private final String host = "localhost";
    private final int port = 8090;
    private PrintWriter output;
    private BufferedReader reader;
    private boolean connected = false;
    private BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));
    private int sequence = 1;
    private ArrayList<String> cmdList;

    public static void main(String[] args) {
        MailboxClient m = new MailboxClient();
        m.connect();

    }

    public void connect() {
        try {
            cmdList = new ArrayList<>();
            cmdList.add("ls");
            cmdList.add("who");
            cmdList.add("exit");
            cmdList.add("msg");
            cmdList.add("login");
            cmdList.add("time");
            sock = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            output = new PrintWriter(sock.getOutputStream());
            connected = true;
            sequence = (int) Math.random() * 1000;

            standBy();
        } catch (IOException ex) {
        }
    }

    public void standBy() {
        System.out.println("Connected to Server: " + sock.getLocalAddress().toString() + " on port " + sock.getPort() + "\n");
        System.out.println("Type of Commands:\n \nlogin <username>\ntime: Local time\nls <Path>\nwho: Users connected\nmsg <Client> <message> : Send Message to Client\nexit: Exit Application");
        System.out.println("\n\nWaiting for Commands: ");
        while (connected == true) {
            waitForMessages();
            waitForCommands();
        }
    }

    private void dispatchCommand(String command, JSONArray parameter) {
        JSONObject j = new JSONObject();
        j.put("sequence", sequence);
        j.put("command", command);
        j.put("parameter", parameter);

        output.println(j);
        output.flush();
    }

    private void waitForMessages() {
        try {
            if (reader.readLine() != null) {
                System.out.println("OK");
            }
        } catch (IOException ex) {
        }
    }

    private boolean valCommand(String command) {
        if (cmdList.contains(command)) {
            return true;
        } else {
            return false;
        }
    }

    private void waitForCommands() {
        try {
            if (cmdReader.readLine() != null) {
                String[] a = cmdReader.readLine().split(" ");
                if (valCommand(a[0]) == true) {
                    String cmd = a[0];
                    JSONArray parameter = new JSONArray();
                    parameter.add(Arrays.copyOfRange(a, 1, a.length));
                    dispatchCommand(cmd, parameter);
                }

            }
        } catch (IOException ex) {

        }
    }
}
