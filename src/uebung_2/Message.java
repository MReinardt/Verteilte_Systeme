package uebung_2;

/**
 * Created by Mikel on 21.11.2015.
 */
public class Message {

    private String user;
    private String message;
    private long date;


    public Message(String user, String message, long date){
        this.user = user;
        this.message = message;
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public long getDate() {
        return date;
    }


}
