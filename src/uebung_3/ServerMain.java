/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uebung_3;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author RonNegi
 */
public class ServerMain {

    private static int port = 1099;

    public static void main(String[] args) {
        createServer(port);
    }

    private static void createServer(int port) {
        System.out.println("Server Build in progress");
        try {
            Registry reg = LocateRegistry.createRegistry(port);
            reg.rebind("Pinnwand", new PinnwandServer());
            System.out.println("Board initiated");
        } catch (RemoteException ex) {
            ex.printStackTrace();

        }

    }
}
