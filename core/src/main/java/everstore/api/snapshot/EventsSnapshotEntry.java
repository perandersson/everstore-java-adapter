package everstore.api.snapshot;

import everstore.api.JournalSize;

import java.util.List;

public final class EventsSnapshotEntry {
    public final JournalSize journalSize;
    public final List<Object> events;

    public EventsSnapshotEntry(JournalSize journalSize, List<Object> events) {
        this.journalSize = journalSize;
        this.events = events;
    }
}
