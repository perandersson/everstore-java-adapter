package everstore.java.snapshot.events.kryo;

import everstore.api.snapshot.EventsSnapshotConfig;
import everstore.api.snapshot.EventsSnapshotManager;
import everstore.api.snapshot.EventsSnapshotManagerFactory;
import everstore.api.snapshot.EverstoreIOException;

import java.io.IOException;

public class JavaKryoSnapshotManagerFactory implements EventsSnapshotManagerFactory {
    @Override
    public EventsSnapshotManager create(EventsSnapshotConfig config) {
        try {
            return new JavaKryoSnapshotManager(config.path, config.cleanOnInt, config.maxBytes);
        } catch (IOException e) {
            throw new EverstoreIOException("Could not create snapshot manager", e);
        }
    }
}
