package everstore.api.exception;

import everstore.api.EverstoreException;

public final class CommitTransactionFailed extends EverstoreException {
    public CommitTransactionFailed(String journalName, Throwable cause) {
        super("Could not commit transaction to journal: " + journalName, cause);
    }
}
