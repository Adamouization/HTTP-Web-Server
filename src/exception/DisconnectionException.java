package exception;

/**
 *
 * Custom Exception class used to throw exceptions when a client unexpectedly disconnects from the server.
 *
 * @author 150014151
 *
 */
public class DisconnectionException extends Exception {

    /**
     * Constructor for the custom DisconnectionException.
     * 
     * @param message String message to display when a new exception is thrown.
     */
    public DisconnectionException(String message) {
        super(message);
    }

}
