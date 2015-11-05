package everstore.vanilla.protocol.messages;

import everstore.vanilla.RequestUID;
import everstore.vanilla.TransactionUID;
import everstore.vanilla.WorkerUID;
import everstore.vanilla.io.EndianAwareOutputStream;
import everstore.vanilla.protocol.DataStoreRequest;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.MessageRequest;

import java.io.IOException;

import static everstore.vanilla.HeaderProperties.NONE;
import static everstore.vanilla.protocol.Constants.INTEGER;
import static everstore.vanilla.protocol.RequestType.ROLLBACK_TRANSACTION;

public final class RollbackTransactionRequest implements MessageRequest {
    public final String journalName;
    public final TransactionUID transactionUID;

    public RollbackTransactionRequest(String journalName, TransactionUID transactionUID) {
        this.journalName = journalName;
        this.transactionUID = transactionUID;
    }

    @Override
    public void write(EndianAwareOutputStream stream) throws IOException {
        stream.putInt(journalName.length());
        stream.putInt(transactionUID.id);
        stream.putString(journalName);
    }

    @Override
    public int size() {
        return INTEGER * 2 + journalName.length();
    }

    public static DataStoreRequest create(String journalName, TransactionUID transactionUID,
                                          RequestUID requestUID, WorkerUID workerUID) {
        final RollbackTransactionRequest body = new RollbackTransactionRequest(journalName, transactionUID);
        final Header header = new Header(ROLLBACK_TRANSACTION, body.size(), requestUID, NONE, workerUID);
        return new DataStoreRequest(header, body);
    }
}
