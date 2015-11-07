package everstore.api.exception;

import everstore.api.EverstoreException;

public class EverstoreSerializerFailed extends EverstoreException {
    public EverstoreSerializerFailed(String message, Throwable cause) {
        super(message, cause);
    }

    public EverstoreSerializerFailed(Throwable cause) {
        super(cause);
    }
}
