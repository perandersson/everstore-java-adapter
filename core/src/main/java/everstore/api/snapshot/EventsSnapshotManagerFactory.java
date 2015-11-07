package everstore.api.snapshot;

public interface EventsSnapshotManagerFactory {
    /**
     *
     * @param config
     * @return
     */
    EventsSnapshotManager create(EventsSnapshotConfig config);
}
