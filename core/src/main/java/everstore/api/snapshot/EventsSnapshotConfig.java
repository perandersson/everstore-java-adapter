package everstore.api.snapshot;

import java.nio.file.Path;

public final class EventsSnapshotConfig {
    private static final long DEFAULT_MAX_BYTES = 100 * 1024 * 1024; // 100 MB

    public final Path path;
    public final long maxBytes;
    public final boolean cleanOnInt;
    public final EventsSnapshotManagerFactory factory;

    public EventsSnapshotConfig(Path path, EventsSnapshotManagerFactory factory) {
        this(path, DEFAULT_MAX_BYTES, true, factory);
    }

    /**
     * Configuration used for the SnapshotManager responsible for loading and saving raw events
     *
     * @param path       The path on the HDD where the snapshots will be saved and loaded from.
     * @param maxBytes   How much HDD is this manager allowed to use until it
     *                   starts to delete snapshots from the HDD.
     * @param cleanOnInt Clean the snapshot directory when the application is being initialized.
     * @param factory    Factory used to create the snapshot manager based on this configuration.
     */
    public EventsSnapshotConfig(Path path, long maxBytes, boolean cleanOnInt,
                                EventsSnapshotManagerFactory factory) {
        this.path = path;
        this.maxBytes = maxBytes;
        this.cleanOnInt = cleanOnInt;
        this.factory = factory;
    }
}
