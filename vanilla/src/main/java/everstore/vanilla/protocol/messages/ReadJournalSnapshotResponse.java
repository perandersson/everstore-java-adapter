package everstore.vanilla.protocol.messages;

import everstore.api.JournalSize;
import everstore.vanilla.protocol.MessageResponse;

import java.util.List;

public final class ReadJournalSnapshotResponse implements MessageResponse {
    public final JournalSize journalSize;
    public final List<Object> events;

    public ReadJournalSnapshotResponse(JournalSize journalSize, List<Object> events) {
        this.journalSize = journalSize;
        this.events = events;
    }
}
