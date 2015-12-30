package everstore.vanilla;

import everstore.api.CommitResult;
import everstore.api.JournalSize;
import everstore.api.Transaction;
import everstore.api.exception.*;
import everstore.api.serialization.Serializer;
import everstore.api.snapshot.EventsSnapshotEntry;
import everstore.api.snapshot.EventsSnapshotManager;
import everstore.vanilla.callback.RequestResponseCallback;
import everstore.vanilla.callback.RequestResponseCallbacks;
import everstore.vanilla.io.EndianAwareOutputStream;
import everstore.vanilla.protocol.DataStoreRequest;
import everstore.vanilla.protocol.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static everstore.api.validation.Validation.require;

public class VanillaDataStorageSender implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(VanillaDataStorageSender.class);

    private final Thread thread;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final EndianAwareOutputStream outputStream;
    private final Serializer serializer;
    private Optional<EventsSnapshotManager> snapshotManager;
    private final RequestResponseCallbacks callbacks;
    private final VanillaDataStorage dataStorage;

    private final AtomicInteger requestUID = new AtomicInteger(0);

    private final LinkedBlockingQueue<DataStoreRequest> requests = new LinkedBlockingQueue<>();

    public VanillaDataStorageSender(final String name, final EndianAwareOutputStream outputStream,
                                    final Serializer serializer, final Optional<EventsSnapshotManager> snapshotManager,
                                    final RequestResponseCallbacks callbacks,
                                    final VanillaDataStorage dataStorage) {
        require(name.length() > 0, "The name for a VanillaDataStorageSender must be set");
        require(outputStream != null, "An outputStream is required for this sender to work");
        require(serializer != null, "A serializer is required for this sender to work");
        require(callbacks != null, "A callbacks container is required for this sender to work");
        require(dataStorage != null, "A dataStorage is required for this sender to work");

        thread = new Thread(this, "VanillaDataStorageSender_" + name);
        thread.setUncaughtExceptionHandler((t, e) -> uncaughtException(e));

        this.outputStream = outputStream;
        this.serializer = serializer;
        this.snapshotManager = snapshotManager;
        this.callbacks = callbacks;
        this.dataStorage = dataStorage;
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                final DataStoreRequest request = requests.poll(1000, TimeUnit.MILLISECONDS);
                if (request != null) {
                    try {
                        switch (request.header.type) {
                            case READ_JOURNAL:
                                processReadJournalRequest(request, (ReadJournalRequest) request.body);
                                break;
                            case COMMIT_TRANSACTION:
                                processCommitTransaction(request, (CommitTransactionPartialRequest) request.body);
                                break;
                            default:
                                request.write(outputStream);
                                outputStream.flush();
                        }
                    } catch (IOException e) {
                        final RequestResponseCallback callback = callbacks.removeAndGet(request.header.requestUID);
                        callback.fail(e);
                        throw e;
                    }
                }
            }
        } catch (Exception e) {
            // Let uncaughtException method handle the error
            throw new RuntimeException(e);
        }
    }

    /**
     * Process the commit request before sending it to the server
     */
    private void processCommitTransaction(DataStoreRequest request, CommitTransactionPartialRequest body) throws IOException {
        final Set<String> types = new HashSet<>();
        final List<String> events = new ArrayList<>();

        body.events.forEach(event -> {
            types.addAll(serializer.convertToTypes(event));
            types.add(event.getClass().getSimpleName());
            events.add(serializer.convertToString(event));
        });

        final DataStoreRequest newRequest = CommitTransactionRequest.create(events, types, body.journalName,
                request.header.requestUID, body.transactionUID, request.header.workerUID);
        newRequest.write(outputStream);
        outputStream.flush();
    }

    /**
     * Process any read requests. This is to read any existing serialized data before performing the actual request
     * to the server.
     */
    private void processReadJournalRequest(DataStoreRequest request, ReadJournalRequest body) throws IOException {
        final Optional<EventsSnapshotEntry> snapshot = snapshotManager.flatMap(m -> m.load(body.journalName, body.offset));
        if (snapshot.isPresent()) {
            // We need to replace the old callback with a new callback. This is because it must contain the new request
            final RequestResponseCallback callback = callbacks.removeAndGet(request.header.requestUID);
            final EventsSnapshotEntry entry = snapshot.get();

            // Recalculate the offset from where to read any new data
            final JournalSize offset;
            if (entry.journalSize.isLargerThan(body.offset))
                offset = entry.journalSize;
            else
                offset = body.offset;

            // Re-add the new request and send it to the server
            final DataStoreRequest newRequest =
                    new DataStoreRequest(request.header, new ReadJournalWithSnapshotRequest(body.journalName,
                            offset, body.journalSize, entry));
            callbacks.add(request.header.requestUID, callback.success, callback.failure, newRequest);
            newRequest.write(outputStream);
            outputStream.flush();
        } else {
            request.write(outputStream);
            outputStream.flush();
        }
    }

    public CompletableFuture<Transaction> openTransaction(final String name) {
        final DataStoreRequest request = NewTransactionRequest.create(name, nextRequestUID());
        final CompletableFuture<Transaction> transaction = new CompletableFuture<>();

        callbacks.add(request.header.requestUID, dsr -> {
            final NewTransactionResponse response = (NewTransactionResponse) dsr.response;
            transaction.complete(new VanillaTransaction(dataStorage, name, response.journalSize, dsr.header.workerUID,
                    response.transactionUID));
        }, (e) -> transaction.completeExceptionally(new OpenTransactionFailed(name, e)), request);

        try {
            requests.put(request);
        } catch (InterruptedException e) {
            throw new OpenTransactionFailed(name, e);
        }
        return transaction;
    }

    public CompletableFuture<CommitResult> commitEvents(final VanillaTransaction transaction, final List<Object> events) {
        final DataStoreRequest request = CommitTransactionPartialRequest.create(events, transaction.name,
                transaction.transactionUID, nextRequestUID(), transaction.workerUID);
        final CompletableFuture<CommitResult> commitResult = new CompletableFuture<>();

        callbacks.add(request.header.requestUID, dsr -> {
            final CommitTransactionResponse response = (CommitTransactionResponse) dsr.response;
            if (response.success)
                commitResult.complete(new CommitResult(true, events, response.journalSize));
            else
                commitResult.complete(new CommitResult(false, events, response.journalSize));
        }, (e) -> commitResult.completeExceptionally(new CommitTransactionFailed(transaction.name, e)), request);

        try {
            requests.put(request);
        } catch (InterruptedException e) {
            throw new CommitTransactionFailed(transaction.name, e);
        }
        return commitResult;
    }

    public CompletableFuture<Boolean> rollbackTransaction(final VanillaTransaction transaction) {
        final DataStoreRequest request = RollbackTransactionRequest.create(transaction.name, transaction.transactionUID,
                nextRequestUID(), transaction.workerUID);
        final CompletableFuture<Boolean> rollbackResult = new CompletableFuture<>();

        callbacks.add(request.header.requestUID, dsr -> {
            final RollbackTransactionResponse response = (RollbackTransactionResponse) dsr.response;
            rollbackResult.complete(response.success);
        }, (e) -> rollbackResult.completeExceptionally(new RollbackFailed(transaction.name, e)), request);

        try {
            requests.put(request);
        } catch (InterruptedException e) {
            throw new RollbackFailed(transaction.name, e);
        }
        return rollbackResult;
    }

    public CompletableFuture<Boolean> journalExists(final String name) {
        final DataStoreRequest request = JournalExistsRequest.create(name, nextRequestUID());
        final CompletableFuture<Boolean> existsResult = new CompletableFuture<>();

        callbacks.add(request.header.requestUID, dsr -> {
            final JournalExistsResponse response = (JournalExistsResponse) dsr.response;
            existsResult.complete(response.exists);
        }, (e) -> existsResult.completeExceptionally(new JournalExistsException(name, e)), request);

        try {
            requests.put(request);
        } catch (InterruptedException e) {
            throw new JournalExistsException(name, e);
        }

        return existsResult;
    }

    public CompletableFuture<List<Object>> readEventsFromJournal(final VanillaTransaction transaction, JournalSize offset) {
        final DataStoreRequest request = ReadJournalRequest.create(transaction.name, offset, transaction.size(),
                nextRequestUID(), transaction.workerUID);
        final CompletableFuture<List<Object>> readResult = new CompletableFuture<>();

        callbacks.add(request.header.requestUID, dsr -> {
            // Prepare and deserialize events raw data into event objects
            final ReadJournalResponse response = (ReadJournalResponse) dsr.response;
            final List<Object> result = new ArrayList<>(response.events.size() + response.snapshottedEvents.size());
            result.addAll(response.snapshottedEvents);
            for (final Event event : response.events)
                result.add(serializer.convertFromString(event.data));

            // Save a snapshot if a snapshot manager is present
            snapshotManager.ifPresent(manager -> manager.save(transaction.name, new EventsSnapshotEntry(transaction.size(), result)));

            // Send the result to the requester
            readResult.complete(result);
        }, (e) -> readResult.completeExceptionally(new ReadEventsFailed(transaction.name, e)), request);

        try {
            requests.put(request);
        } catch (InterruptedException e) {
            throw new ReadEventsFailed(transaction.name, e);
        }
        return readResult;
    }

    private RequestUID nextRequestUID() {
        return new RequestUID(requestUID.getAndIncrement());
    }

    public void close() {
        running.set(false);
        try {
            outputStream.close();
        } catch (IOException e) {
            log.error("Could not close the output stream attached to the socket", e);
        }
        failAllRequests();
    }

    private void failAllRequests() {
        callbacks.removeAll().forEach(c -> c.fail(new ConnectionClosedException()));
    }

    public void start() {
        thread.start();
    }

    private void uncaughtException(final Throwable e) {
        log.error("Unhandled exception in sender thread", e);
        close();
    }

    private void defaultFailure() {

    }
}
