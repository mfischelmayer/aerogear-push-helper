package at.ftec.aerogear.exception;

/**
 * @author Michael Fischelmayer
 */
public class AerogearHelperException extends Exception {

    public AerogearHelperException() {
    }

    public AerogearHelperException(String message) {
        super(message);
    }

    public AerogearHelperException(String message, Throwable cause) {
        super(message, cause);
    }

    public AerogearHelperException(Throwable cause) {
        super(cause);
    }
}
