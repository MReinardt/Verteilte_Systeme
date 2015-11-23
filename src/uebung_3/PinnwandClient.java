/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uebung_3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author RonNegi
 */
public class PinnwandClient {

    private static String password = "";
    private static String host = "";
    private Registry reg;
    private Pinnwand p;
    private boolean loggedIn = false;
    private static int maxMsgLength = 160;
    private static int maxMsg = 20;
    private static int serverPort = 1099;
    private static final String pin = "Pinnwand";
    private static BufferedReader br;

    public static void main(String[] args) {
        try {
            if (args != null && args.length != 0) {
                host = args[0];
                serverPort = Integer.parseInt(args[1]);
            } else {
                host = "localhost";
                serverPort = 1099;
            }
            PinnwandClient pc = new PinnwandClient();
            br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Password?: ");
            password = br.readLine();
            pc.initClient(password);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showMsg() {
        try {
            String[] messages = p.getMessages();
            if (messages.length != 0) {
                int count = 0;
                for (String msg : messages) {
                    System.out.println(count + ": // " + msg);
                    count++;
                }
            } else {
                System.out.println("no Messages");
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private void initClient(String pw) {
        try {
            System.out.println("Connecting to Server: " + host + " Port: " + serverPort);
            reg = LocateRegistry.getRegistry(host, serverPort);
            p = (Pinnwand) reg.lookup(pin);

            if (p.login(pw) == 1) {
                System.out.println("Logged In");
                loggedIn = true;
                standBy();
            }
            if (p.login(pw) == 0) {
                System.out.println("Log-In failed");
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NotBoundException ex) {
            ex.printStackTrace();
        }

    }

    private void standBy() {
        while (loggedIn == true) {
            System.out.println("Specify a Task: ");
            try {
                String befehl = br.readLine();
                switch (befehl) {

                    case "help":
                        System.out.println("List of Tasks available ///////////////\n");
                        System.out.println("getAll //  new  //  count //  get  ");
                        break;
                    case "getAll":
                        if (p.getMessages().length == 0) {
                            System.out.println("No Messages available");
                        } else {
                            int count = 1;
                            for (String s : p.getMessages()) {
                                System.out.println(count + " : " + s);
                                count++;
                            }
                        }
                        break;
                    case "new":
                        if (p.getMessageCount() >= 20) {
                            System.out.println("Message-count max reached");
                            break;
                        }
                        System.out.println("Type in Message: ");
                        String msg = br.readLine();
                        if (msg.length() > 160) {
                            System.out.println("Message can only be 160 char in length!");
                            break;
                        }
                        if (msg.isEmpty() || msg.equals("")) {
                            System.out.println("Empty Message!");
                            break;
                        }
                        p.putMessage(msg);
                        System.out.println("Message added to Board");
                        break;
                    case "count":
                        System.out.println(p.getMessageCount());
                        break;
                    case "get":
                        System.out.println("Index: ");
                        System.out.println(p.getMessage(Integer.parseInt(br.readLine())));
                        break;
                    case "exit":
                        System.out.println("See you soon!");
                        loggedIn = false;
                        break;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

}
