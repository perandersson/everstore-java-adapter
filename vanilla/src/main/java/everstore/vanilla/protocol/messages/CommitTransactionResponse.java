package everstore.vanilla.protocol.messages;

import everstore.api.JournalSize;
import everstore.vanilla.protocol.MessageResponse;

public final class CommitTransactionResponse implements MessageResponse {
    public final boolean success;
    public final JournalSize journalSize;

    public CommitTransactionResponse(boolean success, JournalSize journalSize) {
        this.success = success;
        this.journalSize = journalSize;
    }
}
