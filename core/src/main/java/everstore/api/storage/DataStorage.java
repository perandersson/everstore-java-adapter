package everstore.api.storage;

import everstore.api.CommitResult;
import everstore.api.JournalSize;
import everstore.api.Transaction;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DataStorage {

    CompletableFuture<Transaction> openTransaction(String name);

    CompletableFuture<List<Object>> readEventsFromJournal(Transaction transaction, JournalSize offset);

    CompletableFuture<CommitResult> commitEvents(Transaction transaction, List<Object> events);

    CompletableFuture<Boolean> rollbackTransaction(Transaction transaction);

    CompletableFuture<Boolean> journalExists(String name);

    /**
     * Close the datastorage and cleanup any opened resources
     */
    void close();
}
