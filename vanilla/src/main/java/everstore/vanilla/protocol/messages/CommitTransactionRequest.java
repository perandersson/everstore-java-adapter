package everstore.vanilla.protocol.messages;

import everstore.vanilla.RequestUID;
import everstore.vanilla.TransactionUID;
import everstore.vanilla.WorkerUID;
import everstore.vanilla.io.EndianAwareOutputStream;
import everstore.vanilla.protocol.DataStoreRequest;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.MessageRequest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import static everstore.vanilla.HeaderProperties.NONE;
import static everstore.vanilla.protocol.Constants.INTEGER;
import static everstore.vanilla.protocol.RequestType.COMMIT_TRANSACTION;

public final class CommitTransactionRequest implements MessageRequest {
    public final String eventsAsString;
    public final String typesAsString;
    public final String journalName;
    public final TransactionUID transactionUID;

    public CommitTransactionRequest(String eventsAsString, String typesAsString, String journalName, TransactionUID transactionUID) {
        this.eventsAsString = eventsAsString;
        this.typesAsString = typesAsString;
        this.journalName = journalName;
        this.transactionUID = transactionUID;
    }

    @Override
    public void write(EndianAwareOutputStream stream) throws IOException {
        stream.putInt(journalName.length());
        stream.putInt(typesAsString.length());
        stream.putInt(eventsAsString.length());
        stream.putInt(transactionUID.id);

        stream.putString(journalName);

        stream.putString(typesAsString);
        stream.putString(eventsAsString);
    }

    @Override
    public int size() {
        return INTEGER * 4 + typesAsString.length() + eventsAsString.length() + journalName.length();
    }

    private static String mkString(Iterable<String> i) {
        StringJoiner joiner = new StringJoiner("\n");
        i.forEach(joiner::add);
        return joiner.toString();
    }

    public static DataStoreRequest create(List<String> events, Set<String> types, String journalName,
                                          RequestUID requestUID, TransactionUID transactionUID,
                                          WorkerUID workerUID) {
        final CommitTransactionRequest body = new CommitTransactionRequest(mkString(events), mkString(types),
                journalName, transactionUID);
        final Header header = new Header(COMMIT_TRANSACTION, body.size(), requestUID, NONE, workerUID);
        return new DataStoreRequest(header, body);
    }
}
