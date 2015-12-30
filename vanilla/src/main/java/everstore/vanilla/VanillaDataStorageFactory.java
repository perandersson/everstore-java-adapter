package everstore.vanilla;

import everstore.api.AdapterConfig;
import everstore.api.snapshot.EventsSnapshotManager;
import everstore.api.storage.DataStorage;
import everstore.api.storage.DataStorageFactory;

import java.io.IOException;
import java.util.Optional;

public class VanillaDataStorageFactory implements DataStorageFactory {

    @Override
    public DataStorage create(AdapterConfig config, String name, Optional<EventsSnapshotManager> snapshotManager) throws IOException {
        final VanillaDataStorage storage = new VanillaDataStorage(config.username, config.password, config.hostname,
                config.port, config.bufferSize, name, config.serializer, snapshotManager);
        storage.connect();
        return storage;
    }
}
