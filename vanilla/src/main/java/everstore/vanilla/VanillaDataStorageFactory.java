package everstore.vanilla;

import everstore.api.AdapterConfig;
import everstore.api.storage.DataStorage;
import everstore.api.storage.DataStorageFactory;

import java.io.IOException;

public class VanillaDataStorageFactory implements DataStorageFactory {

    @Override
    public DataStorage create(AdapterConfig config, String name) throws IOException {
        final VanillaDataStorage storage = new VanillaDataStorage(config.username, config.password, config.hostname,
                config.port, config.bufferSize, name, config.serializer, config.eventsSnapshotManager);
        storage.connect();
        return storage;
    }
}
