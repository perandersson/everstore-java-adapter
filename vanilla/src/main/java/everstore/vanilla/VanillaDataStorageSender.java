package everstore.vanilla;

import everstore.api.CommitResult;
import everstore.api.Offset;
import everstore.api.Transaction;
import everstore.api.exception.CommitTransactionFailed;
import everstore.api.exception.OpenTransactionFailed;
import everstore.api.exception.ReadEventsFailed;
import everstore.api.exception.RollbackFailed;
import everstore.api.serialization.Serializer;
import everstore.api.snapshot.EventsSnapshotEntry;
import everstore.api.snapshot.EventsSnapshotManager;
import everstore.vanilla.callback.RequestResponseCallback;
import everstore.vanilla.callback.RequestResponseCallbacks;
import everstore.vanilla.io.EndianAwareOutputStream;
import everstore.vanilla.protocol.DataStoreRequest;
import everstore.vanilla.protocol.DataStoreResponse;
import everstore.vanilla.protocol.messages.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static everstore.api.validation.Validation.require;
import static java.util.Collections.EMPTY_LIST;

public class VanillaDataStorageSender implements Runnable {

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
                        // TODO: Replace this with a normal logging framework
                        e.printStackTrace();
                        callback.fail();
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
        final Optional<EventsSnapshotEntry> snapshot = snapshotManager.flatMap(m -> m.load(body.journalName));
        if (snapshot.isPresent()) {
            final RequestResponseCallback callback = callbacks.removeAndGet(request.header.requestUID);
            final EventsSnapshotEntry entry = snapshot.get();
            // TODO: Add support for partially read the snapshot and then the rest from the server
            final DataStoreResponse response = new DataStoreResponse(request.header,
                    new ReadJournalSnapshotResponse(entry.journalSize, entry.events));
            callback.succeed(response);
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
        }, () -> transaction.completeExceptionally(new OpenTransactionFailed(name)));

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
                commitResult.complete(new CommitResult(true, EMPTY_LIST, response.journalSize));
            else
                commitResult.complete(new CommitResult(false, events, response.journalSize));
        }, () -> commitResult.completeExceptionally(new CommitTransactionFailed(transaction.name)));

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
        }, () -> rollbackResult.completeExceptionally(new RollbackFailed(transaction.name)));

        try {
            requests.put(request);
        } catch (InterruptedException e) {
            throw new RollbackFailed(transaction.name, e);
        }
        return rollbackResult;
    }

    public CompletableFuture<List<Object>> readEventsFromJournal(final VanillaTransaction transaction, Offset offset) {
        final DataStoreRequest request = ReadJournalRequest.create(transaction.name, offset, transaction.size(),
                nextRequestUID(), transaction.workerUID);
        final CompletableFuture<List<Object>> readResult = new CompletableFuture<>();

        callbacks.add(request.header.requestUID, dsr -> {
            final ReadJournalResponse response = (ReadJournalResponse) dsr.response;
            final List<Object> result = new ArrayList<>(response.events.size());
            for (final Event event : response.events)
                result.add(serializer.convertFromString(event.data));

            readResult.complete(result);
        }, () -> readResult.completeExceptionally(new ReadEventsFailed(transaction.name)));

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
            // TODO: Add logging, but ignore otherwise
        }
        failAllRequests();
    }

    private void failAllRequests() {
        callbacks.removeAll().forEach(RequestResponseCallback::fail);
    }

    public void start() {
        thread.start();
    }

    private void uncaughtException(Throwable e) {
        e.printStackTrace();
        close();
    }

    private void defaultFailure() {

    }
}
