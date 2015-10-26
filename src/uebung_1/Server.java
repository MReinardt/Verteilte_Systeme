package uebung_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Mikel Reinhardt, Alessandro Furkim on 05.10.2015.
 */
public class Server {

    /**
     * Serversocket
     */
    private final ServerSocket server;

    /**
     * Konstruktor
     *
     * @param port
     * @throws IOException
     */
    public Server(int port) throws IOException {
        server = new ServerSocket(port);
    }

    /**
     * Diese Methode wartet in einer Endlossschleife auf einen Socket. wenn der
     * Socket akzeptiert wird, ruft diese dann die INOUT Methode auf nachdem die
     * Methode aufgerufen wurde, wird der Socket geschlossen
     */
    private void verbinde() {

        while (true) {
            Socket socket = null;
            try {
                System.out.println("Server wartet auf den Client.");
                socket = server.accept();
                inOut(socket);
            } catch (IOException e) {
                System.out.println("InputOuput problem");
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                        System.out.println("Socket closed");
                    } catch (IOException e) {
                        System.out.println("Socket problem, can't stop the socket");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Diese Methode erwartet einen Socket und holt sich dann deren Input,
     *
     * @param socket
     * @throws IOException
     */
    private void inOut(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream outputServer = new PrintStream(socket.getOutputStream());
        Fibonacci fibonacci = new Fibonacci();
        String string = null;
        try {
            while ((string = bufferedReader.readLine()) != null) {
                int zahl;
                if (isValide(string)) {
                    if ((string = bufferedReader.readLine()) == "") {
                        string = bufferedReader.readLine();
                    }
                    zahl = Integer.parseInt(string);
                    outputServer.println("Die Fibonacci Zahl die von der Eingabe <" + zahl + "> berechnet wurde: <" + fibonacci.fibo(zahl) + ">");
                    System.out.println(bufferedReader.readLine());
                }
            }
        } catch (SocketException e) {
            System.out.println("No Connection to Socket.");
        }
    }

    /**
     * Hier wird der String �ueberprueft
     *
     * @param string
     * @return boolean
     */
    private boolean isValide(String string) {
        if (!string.matches("[0-9]+")) {
            System.out.println(-1);
            return false;
        }

        if (string.equals("")) {
            System.out.println(-1);
            return false;
        }
        try {
            int zahl = Integer.parseInt(string);
            if (zahl > 0 && zahl < 100) {
                return true;
            } else {
                System.out.println(-2);
            }
        } catch (NumberFormatException e) {
            System.out.println(-1);
        }
        return false;
    }

    /**
     * Die main Methode erzeugt einen Serversocket auf dem Port <4444> und ruft
     * dann die Methode verbinde auf
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server(4444);
        server.verbinde();
    }
}
