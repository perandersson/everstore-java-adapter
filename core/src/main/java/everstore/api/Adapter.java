package everstore.api;

import everstore.api.snapshot.EventsSnapshotConfig;
import everstore.api.snapshot.EverstoreIOException;
import everstore.api.storage.DataStorage;
import everstore.api.storage.DataStorageFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static everstore.api.validation.Validation.require;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.walkFileTree;

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
        preConnect();

        final DataStorageFactory factory = config.dataStorageFactory;
        for (int i = 0; i < config.numConnections; ++i) {
            storages.add(factory.create(config, Integer.toString(i)));
        }
    }

    protected void preConnect() {
        config.eventsSnapshotConfig.ifPresent(this::initSnapshotManager);
    }

    private void initSnapshotManager(final EventsSnapshotConfig snapshotConfig) {
        try {
            // Create directory if it doesn't exist
            if (!Files.exists(snapshotConfig.path)) {
                createDirectory(snapshotConfig.path);
            }

            // Ensure that the path is a directory
            require(isDirectory(snapshotConfig.path), "Everstore snapshot path must be a directory");

            // Clean up the snapshot directory if requested
            if (snapshotConfig.cleanOnInt) {
                walkFileTree(snapshotConfig.path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        Files.delete(file);
                        return CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (!dir.equals(snapshotConfig.path)) {
                            Files.delete(dir);
                        }
                        return CONTINUE;
                    }
                });
            }
        } catch (IOException e) {
            throw new EverstoreIOException("Could not initialize the everstore snapshot directory", e);
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
        validateName(name);

        nextStorage++;
        final DataStorage storage = storages.get(nextStorage % storages.size());
        return storage.openTransaction(name);
    }

    /**
     * Check if the supplied journal already exists or not
     *
     * @param name The name of the journal
     * @return TRUE if the supplied journal exists; FALSE otherwise
     */
    public CompletableFuture<Boolean> journalExists(final String name) {
        validateName(name);

        nextStorage++;
        final DataStorage storage = storages.get(nextStorage % storages.size());
        return storage.journalExists(name);
    }

    /**
     * Validate the journal name
     *
     * @param name
     */
    private static void validateName(final String name) {
        require(name.length() > 2 && name.charAt(0) == '/', "Argument 'name' must start with a '/'");
        require(!name.contains(".."), "Argument 'name' cannot contain '..'");
        require(!name.contains("//"), "Argument 'name' cannot contain '//'");
    }
}
