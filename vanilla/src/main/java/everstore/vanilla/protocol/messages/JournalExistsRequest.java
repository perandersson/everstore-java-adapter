package everstore.vanilla.protocol.messages;

import everstore.vanilla.RequestUID;
import everstore.vanilla.io.EndianAwareOutputStream;
import everstore.vanilla.protocol.DataStoreRequest;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.MessageRequest;

import java.io.IOException;

import static everstore.vanilla.HeaderProperties.NONE;
import static everstore.vanilla.WorkerUID.ZERO;
import static everstore.vanilla.protocol.Constants.INTEGER;
import static everstore.vanilla.protocol.RequestType.JOURNAL_EXISTS;

public final class JournalExistsRequest implements MessageRequest {
    public final String journalName;

    public JournalExistsRequest(String journalName) {
        this.journalName = journalName;
    }

    @Override
    public void write(EndianAwareOutputStream stream) throws IOException {
        stream.putInt(journalName.length());
        stream.putString(journalName);
    }

    @Override
    public int size() {
        return INTEGER + journalName.length();
    }

    public static DataStoreRequest create(String journalName, RequestUID requestUID) {
        final JournalExistsRequest body = new JournalExistsRequest(journalName);
        final Header header = new Header(JOURNAL_EXISTS, body.size(), requestUID, NONE, ZERO);
        return new DataStoreRequest(header, body);
    }
}
