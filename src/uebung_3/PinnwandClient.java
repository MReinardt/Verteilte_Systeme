/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uebung_2;

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
    private static String postMsg = "";
    private static String readMsg = "";
    private static String host = "";
    private Registry reg;
    private Pinnwand p;
    private boolean loggedIn = false;

    private static int serverPort = 1090;

    private static BufferedReader br;

    public static void main(String[] args) {
        try {
            if (args != null && args.length != 0) {
                host = args[0];
                serverPort = Integer.parseInt(args[1]);
            } else {
                host = "localhost";
                serverPort = 4444;
            }
            PinnwandClient pc = new PinnwandClient();
            br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Password?: ");
            password = br.readLine();
            pc.initClient(password);

        } catch (IOException ex) {
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
        }
    }

    private void initClient(String pw) {
        try {
            System.out.println("Connectimng to Server: " + host + " Port: " + serverPort);
            reg = LocateRegistry.getRegistry(host, serverPort);
            p = (Pinnwand) reg.lookup("Pinnwand");
            if (p.login(pw) == 6666) {
                System.out.println("Logged In");
                loggedIn = true;
                standBy();
            } else {
                System.out.println("Log-In failed");
            }
        } catch (RemoteException ex) {
        } catch (NotBoundException ex) {
        }

    }

    private void standBy() {
        while (loggedIn == true) {
            System.out.println("Specify a Task: ");
            try {
                String befehl = br.readLine();
                switch (befehl) {
                    case "getAll":
                        break;
                    case "newMsg":
                        break;
                    case "msgCount":
                        break;
                    case "getMsg":
                        break;
                }
            } catch (IOException ex) {
            }

        }
    }

}
