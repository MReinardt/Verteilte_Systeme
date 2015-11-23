package uebung_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by Mikel on 16.11.2015.
 */
public class PinnwandClient {

    private static String userName;
    private static String password;
    private static String host;
    private static int port;

    private final String NAMEOFSERVICE= "Pinnwand";

    private Registry registry;
    private BufferedReader bufferedReader;
    private Pinnwand pinnwand;


    public static void main(String[] args) {
        PinnwandClient pinnwandClient = new PinnwandClient();

        try {
            if (args != null && args.length != 0) {
                host = args[0];
                port = Integer.parseInt(args[1]);
            } else {
                System.out.println("The default value of host <127.0.0.1> and port <1099>");
                host = "127.0.0.1";
                port = 1099;
            }
            pinnwandClient.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Insert your username: ");
            userName = pinnwandClient.bufferedReader.readLine();
            System.out.println("Insert the password: ");
            password = pinnwandClient.bufferedReader.readLine();
            pinnwandClient.init();
        } catch (IOException e) {
            usageString();
            System.exit(1);
        }
    }
    public static void usageString(){
        System.out.println();
    }

    private void init() {
        try {
            //System.out.println("Connecting to Server Host: <" + host + "> Port: <" + port + ">");
            registry = LocateRegistry.getRegistry(host, port);
            pinnwand = (Pinnwand) registry.lookup(NAMEOFSERVICE);

            if (pinnwand.login(password)) {
                System.out.println("Logged In");
                standBy();
            } else {
                System.out.println("Log-In failed");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }

    private void standBy() {
        while (true) {
            System.out.println("(" + userName + ") Specify a Task: ");
            try {
                String input = bufferedReader.readLine();
                switch (input) {
                    case "help":
                        System.out.println("List of tasks available:\n" +
                                "getAll -> get all Messages\n" +
                                "newMsg -> put message on pinnboard\n" +
                                "msgCount -> get the number of activ messages\n" +
                                "getMsg -> get details of the message\n" +
                                "logout -> you disconnect from server\n");
                        break;
                    case "getAll":
                        int count = 1;
                        for (String string : pinnwand.getMessages()) {
                            System.out.println(count + " : " + string);
                            count++;
                        }
                        break;
                    case "newMsg":
                        System.out.println("Type in Message: ");
                        String msg = bufferedReader.readLine();
                        if (msg.length() > 160) {
                            System.out.println("Message can only be 160 char in length!");
                            break;
                        }
                        if (pinnwand.getMessageCount() > 19) {
                            System.out.println("Max Messages :" + 20);
                            break;
                        }
                        if (msg.isEmpty() || msg.equals("")) {
                            System.out.println("Empty Message!");
                            break;
                        }
                        pinnwand.putMessage(msg);
                        System.out.println("Message added to Board");
                        break;
                    case "msgCount":
                        System.out.println(pinnwand.getMessageCount());
                        break;
                    case "getMsg":
                        System.out.println("Index: ");
                        System.out.println(pinnwand.getMessage(Integer.parseInt(bufferedReader.readLine())));
                        break;
                    case "logout":
                        System.out.println("Have a nice day " + userName);
                        System.exit(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
