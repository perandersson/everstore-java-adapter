package everstore.api.snapshot;

import java.nio.file.Path;

public final class EventsSnapshotConfig {
    public final Path path;
    public final long maxBytes;
    public final boolean cleanOnInt;

    public EventsSnapshotConfig(Path path) {
        this(path, 100 * 1024 * 1024, true);
    }

    /**
     * Configuration used for the SnapshotManager responsible for loading and saving raw events
     *
     * @param path       The path on the HDD where the snapshots will be saved and loaded from
     * @param maxBytes   How much HDD is this manager allowed to use until it
     *                   starts to delete snapshots from the HDD
     * @param cleanOnInt Clean the snapshot directory when the application is being initialized
     */
    public EventsSnapshotConfig(Path path, long maxBytes, boolean cleanOnInt) {
        this.path = path;
        this.maxBytes = maxBytes;
        this.cleanOnInt = cleanOnInt;
    }
}
