package uebung_3;

/**
 * Created by Mikel on 23.11.2015.
 */
public class Message {

    private String message;
    private long date;

    public Message(String message, long date){
        this.message = message;
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
