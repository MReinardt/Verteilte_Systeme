package uebung_1;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Mikel Reinhardt, Alessandro Furkim
 * on 05.10.2015.
 */
public class Client {


    static Socket socket = null;
    static String string = "";

    /**
     * Es wird ein Socket erzeugt mit den Portnummer <4444> und die schickt dann mit dem OutputStream
     * die Zahl die berechnet werden soll
     */
    public static void main(String[] args){



        try{
            socket = new Socket("localhost", 4444);

            PrintStream outputClient = new PrintStream(socket.getOutputStream(), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true){
                eingabe();

                outputClient.println(string);

                //Ausgabe der Antwort auf der Kommandozeile
                String serverResponse = null;
                while ((serverResponse = bufferedReader.readLine()) != null) {

                    System.out.println(serverResponse);
                    eingabe();
                }
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
    public static void eingabe(){
        try{
            while(isValide(string) == false){
                System.out.println("Bitte geben sie eine Zahl ein.");
                InputStreamReader inputStreamReader = new InputStreamReader(System.in);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                string = reader.readLine();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static boolean isValide(String string){
        if(string.equals("")){
            return false;
        }
        try{
            int zahl = Integer.parseInt(string);
            System.out.println(zahl);
            if(zahl > 0 && zahl < 100 ){
                return true;
            }
        }catch (NumberFormatException e){
            System.out.println("Ungueltige Eingabe: <" + string + "> ist keine Zahl.");
        }
        return false;
    }
}
