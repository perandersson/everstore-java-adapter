package everstore.api.exception;

import everstore.api.EverstoreException;

public final class OpenTransactionFailed extends EverstoreException {
    public OpenTransactionFailed(final String journalName) {
        super("Could not open transaction for journal: " + journalName);
    }

    public OpenTransactionFailed(final String journalName, Throwable cause) {
        super("Could not open transaction for journal: " + journalName, cause);
    }
}
