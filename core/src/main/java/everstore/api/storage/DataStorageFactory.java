package everstore.api.storage;

import everstore.api.AdapterConfig;

import java.io.IOException;

public interface DataStorageFactory {

    /**
     * Create a new data storage
     *
     * @param config Configuration
     * @param name   A unique name for this data storage
     * @return The new data storage
     */
    DataStorage create(AdapterConfig config, String name) throws IOException;
}
