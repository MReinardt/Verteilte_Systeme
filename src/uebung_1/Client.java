package uebung_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by Mikel Reinhardt, Alessandro Furkim on 05.10.2015.
 */
public class Client {

    static Socket socket = null;
    static String string = "";
    static String host;
    static int port;

    /**
     * Es wird ein Socket erzeugt mit den Portnummer <4444> und die schickt dann
     * mit dem OutputStream die Zahl die berechnet werden soll
     */
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Fehler: fehlende Argumente.\n");
            System.exit(0);
        }

        host = args[0];
        port = Integer.parseInt(args[1]);

        try {
            socket = new Socket(host, port);

            PrintStream outputClient = new PrintStream(socket.getOutputStream(), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                Scanner s = new Scanner(System.in);
                System.out.println("Geben Sie einen Befehl ein: ");
                String cmd = s.next();
                switch (cmd) {
                    case "hilfe":
                        System.out.println("Folgende Befehle sind : 'fibo (berechnet Fibonacci Zahl)'\n 'ende (Schließt die Verbindug zum Server)'");
                        break;
                    case "fibo":
                        eingabe();
                        outputClient.println(string);

                        //Ausgabe der Antwort auf der Kommandozeile
                        String serverResponse = null;
                        while ((serverResponse = bufferedReader.readLine()) != null) {

                            System.out.println(serverResponse);
                            eingabe();
                        }
                        break;
                    case "ende":
                        System.out.println("Socket closed");
                        socket.close();

                }

            }
        } catch (UnknownHostException e) {
            System.out.println("Unknown Host, please check the Host Input");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Input/Output Exception, please check your Input");
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

    public static void eingabe() {
        try {
            while (isValide(string) == false) {
                System.out.println("Bitte geben sie eine Zahl ein.");
                InputStreamReader inputStreamReader = new InputStreamReader(System.in);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                string = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isValide(String string) {
        if (string.equals("")) {
            return false;
        }
        try {
            int zahl = Integer.parseInt(string);
            System.out.println(zahl);
            if (zahl > 0 && zahl < 100) {
                return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ungueltige Eingabe: <" + string + "> ist keine Zahl.");
        }
        return false;
    }
}
