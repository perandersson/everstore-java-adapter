package everstore.api;

import everstore.api.storage.DataStorage;
import everstore.api.storage.DataStorageFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static everstore.api.validation.Validation.require;

public class Adapter {
    /**
     * The configuration used by this adapter
     */
    public final AdapterConfig config;

    private final List<DataStorage> storages = new ArrayList<>();
    private int nextStorage = 0;

    public Adapter(final AdapterConfig config) {
        this.config = config;
    }

    /**
     * Connect this adapter to the server
     */
    public void connect() throws IOException {
        final DataStorageFactory factory = config.dataStorageFactory;
        for (int i = 0; i < config.numConnections; ++i) {
            storages.add(factory.create(config, Integer.toString(i)));
        }
    }

    public void close() {
        storages.forEach(DataStorage::close);
    }

    /**
     * Open a new transaction for a journal. Non-existent journals will be handled as if you are working on an empty journal.
     *
     * @param name The name of the journal. The journal cannot be empty and must start with a leading slash
     * @return A journal
     * @throws IllegalArgumentException If the supplied journal name is invalid.
     */
    public CompletableFuture<Transaction> openTransaction(final String name) {
        require(name.length() > 2 && name.charAt(0) == '/', "Argument 'name' must start with a '/'");
        require(!name.contains(".."), "Argument 'name' cannot contain '..'");
        require(!name.contains("//"), "Argument 'name' cannot contain '//'");

        nextStorage++;
        final DataStorage storage = storages.get(nextStorage % storages.size());
        return storage.openTransaction(name);
    }
}
