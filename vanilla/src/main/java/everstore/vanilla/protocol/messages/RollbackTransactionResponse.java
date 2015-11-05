package everstore.vanilla.protocol.messages;

import everstore.vanilla.protocol.MessageResponse;

public final class RollbackTransactionResponse implements MessageResponse {
    public final boolean success;

    public RollbackTransactionResponse(boolean success) {
        this.success = success;
    }
}
