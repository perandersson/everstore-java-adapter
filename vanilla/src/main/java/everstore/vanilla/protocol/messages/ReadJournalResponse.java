package everstore.vanilla.protocol.messages;

import everstore.api.JournalSize;
import everstore.vanilla.Event;
import everstore.vanilla.protocol.MessageResponse;

import java.util.List;

public final class ReadJournalResponse implements MessageResponse {
    public final JournalSize journalSize;
    public final List<Event> events;

    public ReadJournalResponse(JournalSize journalSize, List<Event> events) {
        this.journalSize = journalSize;
        this.events = events;
    }
}
