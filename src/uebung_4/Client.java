/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uebung_4;

import java.net.Socket;

/**
 * Client
 *
 * @author RonNegi
 */
public class Client {

    public Socket clientSocket;
    public Runnable clientThread;
    public String nick;

    public Client(Socket client, Runnable clientThread, String nick) {
        this.clientSocket = client;
        this.clientThread = clientThread;
        this.nick = nick;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public Runnable getClientThread() {
        return clientThread;
    }

}
