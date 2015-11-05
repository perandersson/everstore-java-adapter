package everstore.api;

public class EverstoreException extends RuntimeException {
    public EverstoreException(String message) {
        super(message);
    }
    public EverstoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
