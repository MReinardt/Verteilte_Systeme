/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uebung_4;

import java.net.Socket;

/**
 *
 * @author RonNegi
 */
public class ConnectionHandler implements Runnable {

    private Socket client;

    public ConnectionHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {

    }
}
