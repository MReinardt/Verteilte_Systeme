package uebung_4;

/**
 * Created by Mikel on 20.12.2015.
 */
public enum StatusCode {

    OK(200),
    OK_NO_RESPONSE(204),

    INCORRECT_REQUEST(400),
    UNAUTHORIZED(401),
    RESOURCE_NOT_FOUND(404),
    TO_MANY_REQUEST(429),

    NOT_IMPLEMENTED(501),
    SERVICE_UNAVAILABLE(503);

    private final int code;

    StatusCode(int code){
        this.code= code;
    }

    public int getCode(){
        return code;
    }

}
