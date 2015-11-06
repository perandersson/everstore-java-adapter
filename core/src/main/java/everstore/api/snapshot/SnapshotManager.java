package everstore.api.snapshot;

import java.io.IOException;
import java.util.Optional;

public interface SnapshotManager<T> {

    /**
     * Save a snapshot with the given name.
     *
     * @param name   The name of the snapshot entry
     * @param object The snapshot entry
     */
    void save(String name, T object) throws IOException;

    /**
     * Try to load a snapshot with the given name
     *
     * @param name The name of the snapshot entry
     * @return A potential snapshot entry
     */
    Optional<T> load(String name) throws IOException;

    /**
     * Retrieves how many bytes this manager is using on the HDD.
     *
     * @return The size in bytes
     */
    long memoryUsed();

}
