package everstore.api.exception;

import everstore.api.EverstoreException;

public final class RollbackFailed extends EverstoreException {
    public RollbackFailed(String journalName, Throwable cause) {
        super("Could not rollback transaction on journal: " + journalName, cause);
    }
}
