package everstore.api;

import everstore.api.serialization.Serializer;
import everstore.api.snapshot.EventsSnapshotManager;
import everstore.api.snapshot.SnapshotManager;
import everstore.api.storage.DataStorageFactory;

import java.util.Optional;

import static everstore.api.validation.Validation.require;
import static java.util.Optional.empty;

/**
 * Configuration used by the adapter to setup the EverStore connection
 */
public final class AdapterConfig {
    public final String username;
    public final String password;
    public final String hostname;
    public final short port;
    public final int numConnections;
    public final int timeout;
    public final int bufferSize;
    public final Serializer serializer;
    public final DataStorageFactory dataStorageFactory;
    public final Optional<SnapshotManager> snapshotManager;
    public final Optional<EventsSnapshotManager> eventsSnapshotManager;

    public AdapterConfig(String username, String password, String hostname, short port, int numConnections,
                         Serializer serializer, DataStorageFactory dataStorageFactory) {
        this(username, password, hostname, port, numConnections, 2000, 65526, serializer, dataStorageFactory, empty(), empty());
    }

    public AdapterConfig(String username, String password, String hostname, short port, int numConnections,
                         int timeout, int bufferSize, Serializer serializer,
                         DataStorageFactory dataStorageFactory, Optional<SnapshotManager> snapshotManager,
                         Optional<EventsSnapshotManager> eventsSnapshotManager) {
        require(hostname.length() > 0, "You must supply a valid hostname");
        require(port > 0, "The port used for connecting to the server must be a valid port");
        require(numConnections > 0, "The number of connections must be larger than 0");
        require(bufferSize > 0, "The buffer used for sending and receiving events must be larger than 0");
        require(serializer != null, "A serializer is required");
        require(dataStorageFactory != null, "A dataStorageFactory is required");

        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.numConnections = numConnections;
        this.timeout = timeout;
        this.bufferSize = bufferSize;
        this.serializer = serializer;
        this.dataStorageFactory = dataStorageFactory;
        this.snapshotManager = snapshotManager;
        this.eventsSnapshotManager = eventsSnapshotManager;
    }

}
