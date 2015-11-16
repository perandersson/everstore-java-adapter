package everstore.java.snapshot.events;

import everstore.api.snapshot.EventsSnapshotManagerFactory;
import everstore.java.snapshot.events.kryo.JavaKryoSnapshotManagerFactory;

public final class SnapshotManagers {
    public static EventsSnapshotManagerFactory defaultFactory() {
        return new JavaKryoSnapshotManagerFactory();
    }
}
