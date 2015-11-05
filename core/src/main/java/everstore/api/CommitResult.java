package everstore.api;

import java.util.List;

public final class CommitResult {
    public final boolean success;
    public final List<Object> events;
    public final JournalSize journalSize;

    public CommitResult(boolean success, List<Object> events, JournalSize journalSize) {
        this.success = success;
        this.events = events;
        this.journalSize = journalSize;
    }
}
