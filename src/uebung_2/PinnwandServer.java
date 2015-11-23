package uebung_2;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mikel and Alessandro on 16.11.2015.
 */
public class PinnwandServer extends UnicastRemoteObject implements Pinnwand {

    final int MAXNUMMASSAGES = 20;
    final int MESSAGELIFETIME = 20;
    final int MAXLENGTHMESSAGE = 160;
    static final String NAMEOFSERVICE = "Pinnwand";
    final String PASSWORD = "password";

    private ArrayList<Message> messageList = new ArrayList();

    public PinnwandServer() throws RemoteException{
        lifeTimeCheck();
    }

    @Override
    public boolean login(String password) throws RemoteException {
        if (password.equals(PASSWORD)) {
            try{
                System.out.println(RemoteServer.getClientHost());
            } catch (ServerNotActiveException e){
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getMessageCount() throws RemoteException {
        return messageList.size();
    }

    @Override
    public String[] getMessages() throws RemoteException {
        String[] msgArray = new String[messageList.size()];
        for (int i = 0; i < messageList.size(); i++) {
            Message message = messageList.get(i);
            msgArray[i] = message.getMessage() +"Test: " + message.getDate();
        }
        return msgArray;
    }

    @Override
    public String getMessage(int index) throws RemoteException {
        Message message = messageList.get(index);
        return message.getMessage();
    }

    @Override
    public boolean putMessage(String msg) throws RemoteException {
        if(getMessageCount() > MAXNUMMASSAGES){
            return false;
        }
        if (msg != "" || !msg.isEmpty() || msg != " " || msg.length() <= MAXLENGTHMESSAGE) {
            messageList.add(new Message("testUser",msg, System.nanoTime()));
            return true;
        } else {
            return false;
        }

    }
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private void lifeTimeCheck() {
        System.out.println("TimeChecker is starting");
        Runnable lifeTimeChecker = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < messageList.size(); i++) {
                    Long time = (System.nanoTime() / 1000000000);
                    Message message = messageList.get(i);
                    if (time > ((message.getDate() / 1000000000) + MESSAGELIFETIME)) {
                        System.out.println("Message :" + message.getMessage() + " removed.");
                        messageList.remove(i);
                    }
                }
            }
        };
        scheduler.scheduleAtFixedRate(lifeTimeChecker, 5, 5, TimeUnit.SECONDS);
    }

    public static void quit(){
        System.out.println("Server is closing");
        try {
            Registry registry = LocateRegistry.getRegistry();
            registry.unbind(NAMEOFSERVICE);
        } catch (Exception e) {
            System.out.println("Problem with quit server");
        }
    }

    public static void main(String[] args) {
        System.out.println("Server build in progress");
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(NAMEOFSERVICE, new PinnwandServer());
            System.out.println("Board initiated");
        } catch (RemoteException e) {
            e.printStackTrace();
            quit();
            System.exit(1);
        }
    }
}
