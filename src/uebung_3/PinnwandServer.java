/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uebung_2;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author RonNegi
 */
public class PinnwandServer implements Pinnwand {

    private ArrayList<String> pinList = new ArrayList();
    private ArrayList<Long> lifeTime = new ArrayList();

    protected PinnwandServer() {
    }

    @Override
    public int login(String password) throws RemoteException {
        String pw = "password";
        if (password.equals(pw)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getMessageCount() throws RemoteException {
        return pinList.size();
    }

    @Override
    public String[] getMessages() throws RemoteException {
        String[] msgArray = new String[pinList.size()];
        for (int i = 0; i <= pinList.size(); i++) {
            msgArray[i] = pinList.get(i);
        }
        return msgArray;
    }

    @Override
    public String getMessage(int index) throws RemoteException {
        return pinList.get(index);
    }

    @Override
    public boolean putMessage(String msg) throws RemoteException {
        if (msg != "" || !msg.isEmpty() || msg != " ") {
            pinList.add(msg);
            lifeTime.add(System.currentTimeMillis());
            return true;
        } else {
            return false;
        }

    }

}
