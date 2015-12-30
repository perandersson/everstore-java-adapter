package everstore.api.storage;

import everstore.api.AdapterConfig;
import everstore.api.snapshot.EventsSnapshotManager;

import java.io.IOException;
import java.util.Optional;

public interface DataStorageFactory {

    /**
     * Create a new data storage
     *
     * @param config          Configuration
     * @param name            A unique name for this data storage
     * @param snapshotManager A potential snapshot manager
     * @return The new data storage
     */
    DataStorage create(AdapterConfig config, String name, Optional<EventsSnapshotManager> snapshotManager) throws IOException;
}
