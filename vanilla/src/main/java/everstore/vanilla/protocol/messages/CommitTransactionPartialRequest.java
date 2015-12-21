package everstore.vanilla.protocol.messages;

import everstore.vanilla.RequestUID;
import everstore.vanilla.TransactionUID;
import everstore.vanilla.WorkerUID;
import everstore.vanilla.io.EndianAwareOutputStream;
import everstore.vanilla.protocol.DataStoreRequest;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.MessageRequest;

import java.util.List;

import static everstore.vanilla.HeaderProperties.NONE;
import static everstore.vanilla.protocol.RequestType.COMMIT_TRANSACTION;

public final class CommitTransactionPartialRequest implements MessageRequest {
    public final List<Object> events;
    public final String journalName;
    public final TransactionUID transactionUID;

    public CommitTransactionPartialRequest(List<Object> events, String journalName, TransactionUID transactionUID) {
        this.events = events;
        this.journalName = journalName;
        this.transactionUID = transactionUID;
    }

    @Override
    public void write(EndianAwareOutputStream stream) {
        // Don't do anything
    }

    @Override
    public int size() {
        return 0;
    }

    public static DataStoreRequest create(List<Object> events, String journalName, TransactionUID transactionUID,
                                          RequestUID requestUID, WorkerUID workerUID) {
        final CommitTransactionPartialRequest request = new CommitTransactionPartialRequest(events, journalName,
                transactionUID);
        final Header header = new Header(COMMIT_TRANSACTION, 0, requestUID, NONE, workerUID);
        return new DataStoreRequest(header, request);

    }
}
