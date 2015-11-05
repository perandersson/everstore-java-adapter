package everstore.api.storage;

import everstore.api.CommitResult;
import everstore.api.Offset;
import everstore.api.Transaction;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DataStorage {

    CompletableFuture<Transaction> openTransaction(String name);

    CompletableFuture<List<Object>> readEventsFromJournal(Transaction transaction, Offset offset);

    CompletableFuture<CommitResult> commitEvents(Transaction transaction, List<Object> events);

    CompletableFuture<Boolean> rollbackTransaction(Transaction transaction);

    /**
     * Close the datastorage and cleanup any opened resources
     */
    void close();
}
