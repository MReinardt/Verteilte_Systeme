package uebung_1;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Mikel Reinhardt, Alessandro Furkim
 * on 05.10.2015.
 */
public class Client {


    private static Socket socket;
    private static String string = "";
    private static int zahl;
    private static String serverResponse;
    private static String host;
    private static int port;
    private static PrintStream outputClient;
    private static BufferedReader bufferedReader;


    private static final String DEFAULTHOST = "localhost";
    private static final int DEFAULTPORT = 5678;
    private static final String USAGE = "Usage: Client.jar [host] [port (Optional! Defaul: 5678)]";

    /**
     * Es wird ein Socket erzeugt mit den Portnummer <4444> und die schickt dann mit dem OutputStream
     * die Zahl die berechnet werden soll
     */
    public static void main(String[] args) {

        if (args.length == 1) {
            host = args[0];
            port = DEFAULTPORT;
        } else if (args.length != 2){
            System.out.println(USAGE);
            System.exit(1);
        } else {
            try {
                host = args[0];
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port! >> " + USAGE);
                System.exit(1);
            }
        }
        try{
            while(true){
                System.out.println("Please insert a number. (<help> for info)");
                InputStreamReader inputStreamReader = new InputStreamReader(System.in);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                string = reader.readLine();
                isValide(string);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void isValide(String string) {
        if (string.equals("exit")) {
            System.out.println("Programm finished.");
            System.exit(1);
        } else if (string.equals("help")) {
            System.out.println("compute <number> -> calculated the fibonacci number");
            System.out.println("help -> show the command lines");
            System.out.println("exit -> finish the programm");
        } else if (string.contains("compute")) {
            try {
                String[] temp = string.split(" ");
                zahl = Integer.parseInt(temp[1]);
                if (zahl > 0 && zahl < 100) {
                    verbindung();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: <" + string + "> is no number.");
            }
        }
    }

    public static void verbindung(){
        try{
            socket = new Socket(host, port);

            outputClient = new PrintStream(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputClient.println(zahl);

            while ((serverResponse = bufferedReader.readLine()) != null) {
                System.out.println("The calculated fibonacci number is: " + serverResponse);
                break;
            }
        } catch (UnknownHostException e){
            System.out.println("Unknown Host, please check the Host Input");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("Input/Output Exception, please check your Input");
            e.printStackTrace();
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
