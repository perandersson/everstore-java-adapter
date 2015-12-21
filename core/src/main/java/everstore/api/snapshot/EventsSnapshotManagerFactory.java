package everstore.api.snapshot;

public interface EventsSnapshotManagerFactory {
    /**
     * Create a new snapshot manager for managing events
     *
     * @param config Configuration used to create the snapshot manager
     * @return A created snapshot manager
     */
    EventsSnapshotManager create(EventsSnapshotConfig config);
}
