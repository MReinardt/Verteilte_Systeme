package uebung_4;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mikel on 14.12.2015.
 */
public class Client {

    public PrintWriter toServer;
    public BufferedReader fromServer;
    private Socket socket;
    public int sequence;

    private InetAddress address;
    private int port;
    private String temp;
    private final int DEFAULT_PORT = 4444;

    public Client(String[] args) throws IOException {

        if(args.length == 2){
            address = InetAddress.getByName(args[0]);
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Port muss ein integer werte sein.");
                System.exit(1);
            }
        } else if (args.length > 2) {
            System.err.println("Usage: <ip> <port> (Default: <localhost> <6060>)");
            System.exit(1);
        } else {
            address = InetAddress.getLocalHost();
            temp = "141.64.166.9";
            port = DEFAULT_PORT;
        }

        socket = new Socket(address, port);

        // To server
        toServer = new PrintWriter(socket.getOutputStream(), true);

        // From server
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(dialog()).start();

        String[] output;
        while (!socket.isClosed()) {
            output = decode(fromServer.readLine());
            for (String out : output) {
                if (out.equals("Bye.")) {
                    System.out.println(out);
                    socket.close();
                    System.exit(1);
                } else if (!out.equals("")) {
                    System.out.println(out);
                }
            }
        }
    }

    private Runnable dialog() {
        return () -> {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String inputUser;

            while (!socket.isClosed()) {
                try {
                    inputUser = in.readLine();
                    if (inputUser != null) {
                        toServer.println(encode(inputUser));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private JSONObject encode(String in) {
        JSONObject obj = new JSONObject();

        List<String> list = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(in);
        while (m.find()) {
            list.add(m.group(1));
        }
        obj.put("sequence", sequence);

        if (!list.isEmpty()) {
            obj.put("command", list.get(0));
        }

        JSONArray params = new JSONArray();
        for (int i = 1; i < list.size(); i++) {
            params.add(list.get(i));
        }

        obj.put("params", params);

        return obj;
    }

    private String[] decode(String out) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(out);
            Object[] paramsObj = ((JSONArray) obj.get("response")).toArray();
            String[] response = Arrays.copyOf(paramsObj, paramsObj.length, String[].class);
            sequence = ((Long) obj.get("sequence")).intValue() + 1;
            return response;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            return new String[]{};
        }
        return new String[]{ "JSON fehler!" };
    }

    public static void main(String[] args) throws IOException {
        new Client(args);
    }
}