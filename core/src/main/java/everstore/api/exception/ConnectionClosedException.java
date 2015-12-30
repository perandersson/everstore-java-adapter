package everstore.api.exception;

import everstore.api.EverstoreException;

public final class ConnectionClosedException extends EverstoreException {
    public ConnectionClosedException() {
        super("The connection is already closed");
    }
}
