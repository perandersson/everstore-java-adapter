package everstore.api.exception;

import everstore.api.EverstoreException;

public class JournalExistsException extends EverstoreException {
    public JournalExistsException(String message) {
        super(message);
    }

    public JournalExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
