package everstore.vanilla.protocol.messages;

import everstore.api.JournalSize;
import everstore.api.Offset;
import everstore.vanilla.HeaderProperties;
import everstore.vanilla.RequestUID;
import everstore.vanilla.WorkerUID;
import everstore.vanilla.io.EndianAwareOutputStream;
import everstore.vanilla.protocol.*;

import java.io.IOException;

import static everstore.vanilla.HeaderProperties.NONE;
import static everstore.vanilla.protocol.RequestType.READ_JOURNAL;

public final class ReadJournalRequest implements MessageRequest {
    public final String journalName;
    public final Offset offset;
    public final JournalSize journalSize;

    public ReadJournalRequest(String journalName, Offset offset, JournalSize journalSize) {
        this.journalName = journalName;
        this.offset = offset;
        this.journalSize = journalSize;
    }

    @Override
    public void write(EndianAwareOutputStream stream) throws IOException {
        stream.putInt(journalName.length());
        stream.putInt(offset.value);
        stream.putInt(journalSize.value);
        stream.putString(journalName);
    }

    @Override
    public int size() {
        return Constants.INTEGER * 3 + journalName.length();
    }

    public static DataStoreRequest create(String journalName, Offset offset, JournalSize journalSize,
                                          RequestUID requestUID, WorkerUID workerUID) {
        final ReadJournalRequest body = new ReadJournalRequest(journalName, offset, journalSize);
        final Header header = new Header(READ_JOURNAL, body.size(), requestUID, NONE, workerUID);
        return new DataStoreRequest(header, body);
    }
}
