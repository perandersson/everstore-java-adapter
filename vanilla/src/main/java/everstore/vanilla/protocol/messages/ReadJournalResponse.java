package everstore.vanilla.protocol.messages;

import everstore.vanilla.Event;
import everstore.vanilla.protocol.MessageResponse;

import java.util.ArrayList;
import java.util.List;

public final class ReadJournalResponse implements MessageResponse {
    public final List<Event> events;
    public final List<Object> snapshottedEvents;

    public ReadJournalResponse(List<Event> events) {
        this.events = events;
        this.snapshottedEvents = new ArrayList<>();
    }

    public ReadJournalResponse(List<Event> events, List<Object> snapshottedEvents) {
        this.events = events;
        this.snapshottedEvents = snapshottedEvents;
    }
}
