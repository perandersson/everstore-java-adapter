package everstore.vanilla.protocol.messages;

import everstore.api.JournalSize;
import everstore.api.snapshot.EventsSnapshotEntry;
import everstore.vanilla.RequestUID;
import everstore.vanilla.WorkerUID;
import everstore.vanilla.io.EndianAwareOutputStream;
import everstore.vanilla.protocol.Constants;
import everstore.vanilla.protocol.DataStoreRequest;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.MessageRequest;

import java.io.IOException;

import static everstore.vanilla.HeaderProperties.NONE;
import static everstore.vanilla.protocol.RequestType.READ_JOURNAL;

public final class ReadJournalWithSnapshotRequest implements MessageRequest {
    public final String journalName;
    public final JournalSize offset;
    public final JournalSize journalSize;
    public final EventsSnapshotEntry snapshotEntry;

    public ReadJournalWithSnapshotRequest(String journalName, JournalSize offset, JournalSize journalSize, EventsSnapshotEntry snapshotEntry) {
        this.journalName = journalName;
        this.offset = offset;
        this.journalSize = journalSize;
        this.snapshotEntry = snapshotEntry;
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
}
