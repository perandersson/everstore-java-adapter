package everstore.vanilla.protocol.messages;

import everstore.vanilla.Event;
import everstore.vanilla.protocol.MessageResponse;

import java.util.List;

public final class ReadJournalResponse implements MessageResponse {
    public final List<Event> events;

    public ReadJournalResponse(List<Event> events) {
        this.events = events;
    }
}
