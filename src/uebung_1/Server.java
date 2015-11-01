package uebung_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Mikel Reinhardt, Alessandro Furkim
 * on 05.10.2015.
 */
public class Server {

    private ServerSocket server;
    private String  input;
    private int zahl;

    private static int port;

    private BufferedReader bufferedReader;
    private PrintStream outputServer;

    public static final int DEFAULTPORT = 5678;
    public static final String USAGE = "Usage: Server.jar [port (Optional! Defaul: 5678)]";


    public Server(int port)throws IOException{
        server = new ServerSocket(port);
    }

    /**
     * Diese Methode wartet in einer Endlossschleife auf einen Socket.
     * wenn der Socket akzeptiert wird, ruft diese dann die INOUT Methode auf
     * nachdem die Methode aufgerufen wurde, wird der Socket geschlossen
     */
    private void verbinde(){

        while (true){
            Socket socket = null;
            try {
                System.out.println("Server waiting for client.");
                socket = server.accept();
                inOut(socket);
            } catch (IOException e){
                System.err.println("IO problem");
                System.exit(1);
            } finally {
                if(socket != null){
                    try{
                        socket.close();
                        System.out.println("Socket closed");
                    } catch (IOException e){
                        System.out.println("Socket problem, can't stop the socket");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Diese Methode erwartet einen Socket und holt sich dann deren Input,
     * @param socket
     * @throws IOException
     */
    private void inOut(Socket socket) throws IOException{
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputServer = new PrintStream(socket.getOutputStream());
        Fibonacci fibonacci = new Fibonacci();
        try{
            while ((input = bufferedReader.readLine()) != null){
                if(isValide(input)){
                    outputServer.println(fibonacci.fibo(zahl));
                }
                continue;
            }
        } catch (SocketException e){
            System.out.println("No Connection to Socket.");
        }
    }
    /**
     * Hier wird der String überprüft
     * @param string
     * @return boolean
     */
    private boolean isValide(String string){
        if(string.equals("")){
            return false;
        }
        try{
            zahl = Integer.parseInt(string);
            if(zahl < 0 || zahl > 55 ){
                outputServer.println("-2 : invalid number range");
                return false;
            }
        }catch (NumberFormatException e){
            outputServer.println("-1 : incorrect input");
        }
        return true;
    }

    /**
     * Die main Methode erzeugt einen Serversocket auf dem Port der als Parameter
     * übergeben wurde oder nimmt den Default Port wenn kein Parameter übergeben wurde
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {

        if(args.length == 0){
            System.out.println("Default port: 5678");
           port = DEFAULTPORT;
        } else if (args.length == 1){
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e){
                System.err.println("Invalid port! >> " + USAGE);
                System.exit(1);
            }
        } else {
            System.out.println(USAGE);
            System.exit(1);
        }
        try {
            Server server = new Server(port);
            server.verbinde();
        } catch (IOException e){
            System.err.println("IO problem!");
            System.exit(1);
        }
    }
}
