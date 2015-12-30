package everstore.vanilla;

import everstore.api.CommitResult;
import everstore.api.JournalSize;
import everstore.api.Transaction;
import everstore.api.serialization.Serializer;
import everstore.api.snapshot.EventsSnapshotConfig;
import everstore.api.snapshot.EventsSnapshotManager;
import everstore.api.storage.DataStorage;
import everstore.vanilla.callback.RequestResponseCallbacks;
import everstore.vanilla.io.EndianAwareInputStream;
import everstore.vanilla.io.EndianAwareOutputStream;
import everstore.vanilla.protocol.messages.AuthenticateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class VanillaDataStorage implements DataStorage {
    private static final Logger log = LoggerFactory.getLogger(VanillaDataStorage.class);

    private final Socket client;

    private final String username;
    private final String password;
    private final String name;
    private final Serializer serializer;
    private final Optional<EventsSnapshotManager> snapshotManager;

    private final RequestResponseCallbacks callbacks = new RequestResponseCallbacks();

    private VanillaDataStorageReceiver receiver;
    private VanillaDataStorageSender sender;

    public VanillaDataStorage(String username, String password,
                              final String hostname, final short port, final int bufferSize,
                              final String name, final Serializer serializer, final Optional<EventsSnapshotManager> snapshotManager) throws IOException {
        final InetAddress address = InetAddress.getByName(hostname);
        this.client = new Socket(address, port);
        this.client.setSendBufferSize(bufferSize);
        this.client.setReceiveBufferSize(bufferSize);

        this.username = username;
        this.password = password;
        this.name = name;
        this.serializer = serializer;
        this.snapshotManager = snapshotManager;//eventsSnapshotConfig.map(c -> c.factory.create(c));
    }

    @Override
    public CompletableFuture<Transaction> openTransaction(final String name) {
        return sender.openTransaction(name);
    }

    @Override
    public CompletableFuture<List<Object>> readEventsFromJournal(final Transaction transaction, final JournalSize offset) {
        return sender.readEventsFromJournal((VanillaTransaction) transaction, offset);
    }

    @Override
    public CompletableFuture<CommitResult> commitEvents(final Transaction transaction, final List<Object> events) {
        return sender.commitEvents((VanillaTransaction) transaction, events);
    }

    @Override
    public CompletableFuture<Boolean> rollbackTransaction(final Transaction transaction) {
        return sender.rollbackTransaction((VanillaTransaction) transaction);
    }

    @Override
    public CompletableFuture<Boolean> journalExists(final String name) {
        return sender.journalExists(name);
    }

    @Override
    public void close() {
        try {
            if (receiver != null)
                receiver.close();
        } catch (Exception e) {
            log.debug("Could not close receiver", e);
        }

        try {
            if (sender != null)
                sender.close();
        } catch (Exception e) {
            log.debug("Could not close sender", e);
        }

        try {
            if (client != null)
                client.close();
        } catch (Exception e) {
            log.debug("Could not close client", e);
        }
    }

    /**
     * Open a connection to the server
     */
    public void connect() throws IOException {
        final ServerProperties serverProperties = readServerProperties();

        final EndianAwareInputStream inputStream = serverProperties.createInputStream();
        final EndianAwareOutputStream outputStream = serverProperties.createOutputStream();

        if (serverProperties.authenticateRequired)
            tryLogin(outputStream, username, password);

        sender = new VanillaDataStorageSender(name, outputStream, serializer, snapshotManager, callbacks, this);
        receiver = new VanillaDataStorageReceiver(name, inputStream, callbacks);

        sender.start();
        receiver.start();
    }

    private void tryLogin(final EndianAwareOutputStream outputStream, final String username,
                          final String password) throws IOException {
        AuthenticateRequest.create(username, password).write(outputStream);
        outputStream.flush();
    }

    private ServerProperties readServerProperties() throws IOException {
        final InputStream stream = client.getInputStream();

        // Read if the server requires big- or little endian for multi-byte numbers
        final ByteOrder byteOrder;
        if (stream.read() == 1)
            byteOrder = ByteOrder.BIG_ENDIAN;
        else
            byteOrder = ByteOrder.LITTLE_ENDIAN;

        final int version = stream.read();
        final boolean authenticateRequired = stream.read() == 1;

        return new ServerProperties(byteOrder, version, authenticateRequired, stream, client.getOutputStream());
    }
}
