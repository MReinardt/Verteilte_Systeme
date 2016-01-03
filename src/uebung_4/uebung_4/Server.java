package uebung_4;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Mikel on 20.12.2015.
 */
public class Server implements Runnable {

    private int port;
    private ServerSocket serverSocket;
    public ArrayList<ServerThread> clients;

    private final int DEFAULT_PORT = 4444;


    public Server(String[] args) {

        if(args.length == 1){
            port = Integer.parseInt(args[0]);
        } else if (args.length > 1) {
            System.err.println("Usage: java Server <port> (DefaultPort: 6060)");
            System.exit(1);
        } else {
            port = DEFAULT_PORT;
        }
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running...");

        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(1);
        }

        clients = new ArrayList<>();
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket client = serverSocket.accept();
                ServerThread thread = new ServerThread(this, client);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Server(args);
    }
}