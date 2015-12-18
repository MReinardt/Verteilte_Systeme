/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uebung_4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * MailBox Server
 *
 * @author RonNegi
 */
public class MailboxServer {

    private static String host;
    private static int port;

    private static ServerSocket serverSocket = null;
    private static Socket client = null;
    private static HashMap<String, Client> clientList;

    private static final int CLIENT_MAX = 6;

    public static void main(String[] args) {
        port = 8090;
        startServer(port);
    }

    /**
     * Start the server for a given Port
     *
     * @param port
     */
    private static void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Stand By for incoming Connections");
            standBy();
        } catch (IOException ex) {
        }
    }

    /**
     * Stand By for Incoming Connections
     */
    private static void standBy() {
        while (true) {
            try {
                client = serverSocket.accept();
                System.out.println("Connection established. Waiting for Username");
                new Thread(new ConnectionHandler(client)).start();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Returns Client for Client-nickName
     *
     * @param nick
     * @return Client
     */
    private static Client getClient(String nick) {
        return clientList.get(nick);
    }

    /**
     * Returns Client for given Client Thread
     *
     * @param runnable
     * @return
     */
    private static Client getClient(Runnable runnable) {
        for (Client c : clientList.values()) {
            if (c.getClientThread().equals(runnable)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Returns all connected Users as String
     *
     * @return users
     */
    private static String getUsersAsString() {
        String users = "";
        if (clientList.size() == 0) {
            return "no connected clients";
        }
        for (String userNick : clientList.keySet()) {
            users = users + "\n" + userNick;
        }
        return users;
    }

    /**
     * Returns Number of Connected Clients
     *
     * @return
     */
    private static int getNumberClients() {
        return clientList.size();
    }

    /**
     * Removes Client from ClientList for a given Nick Name
     *
     * @param nick
     */
    private static void removeClient(String nick) {
        clientList.remove(nick);
    }

    /**
     * Add CLient to Client list
     *
     * @param nick
     * @param client
     */
    private static void addClientToList(String nick, Client client) {
        if (clientList.size() <= 6) {
            clientList.put(nick, client);
        } else {
            System.out.println("Clientlist is full! Max 6");
        }
    }

}
