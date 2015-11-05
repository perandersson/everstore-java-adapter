package everstore.vanilla.protocol.messages;

import everstore.api.JournalSize;
import everstore.vanilla.TransactionUID;
import everstore.vanilla.protocol.MessageResponse;

public final class NewTransactionResponse implements MessageResponse {
    public final JournalSize journalSize;
    public final TransactionUID transactionUID;

    public NewTransactionResponse(JournalSize journalSize, TransactionUID transactionUID) {
        this.journalSize = journalSize;
        this.transactionUID = transactionUID;
    }
}
