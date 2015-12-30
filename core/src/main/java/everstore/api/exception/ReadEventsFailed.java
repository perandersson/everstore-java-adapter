package everstore.api.exception;

import everstore.api.EverstoreException;

public final class ReadEventsFailed extends EverstoreException {
    public ReadEventsFailed(String journalName, Throwable cause) {
        super("Could not read events from journal: " + journalName, cause);
    }
}
