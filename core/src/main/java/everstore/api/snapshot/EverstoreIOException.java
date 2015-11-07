package everstore.api.snapshot;

import everstore.api.EverstoreException;

public class EverstoreIOException extends EverstoreException {
    public EverstoreIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public EverstoreIOException(Throwable cause) {
        super(cause);
    }
}
