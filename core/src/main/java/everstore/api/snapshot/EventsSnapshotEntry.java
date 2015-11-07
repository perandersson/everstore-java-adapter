package everstore.api.snapshot;

import everstore.api.JournalSize;

import java.util.ArrayList;
import java.util.List;

import static everstore.api.JournalSize.ZERO;

public final class EventsSnapshotEntry {
    public final JournalSize journalSize;
    public final List<Object> events;

    protected EventsSnapshotEntry() {
        journalSize = ZERO;
        events = new ArrayList<>();
    }

    public EventsSnapshotEntry(JournalSize journalSize, List<Object> events) {
        this.journalSize = journalSize;
        this.events = events;
    }
}
