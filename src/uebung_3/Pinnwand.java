/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uebung_2;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author RonNegi
 */
public interface Pinnwand extends Remote {

    public int login(String password) throws RemoteException;

    public int getMessageCount() throws RemoteException;

    public String[] getMessages() throws RemoteException;

    public String getMessage(int index) throws RemoteException;

    public boolean putMessage(String msg) throws RemoteException;
}
