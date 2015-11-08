package everstore.vanilla;

import everstore.api.CommitResult;
import everstore.api.JournalSize;
import everstore.api.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static everstore.api.JournalSize.ZERO;

public class VanillaTransaction implements Transaction {
    public final String name;
    public final TransactionUID transactionUID;
    public final WorkerUID workerUID;

    private final VanillaDataStorage dataStorage;
    private final JournalSize journalSize;

    private List<Object> newEvents = new ArrayList<>();

    public VanillaTransaction(VanillaDataStorage dataStorage, String name, JournalSize journalSize,
                              WorkerUID workerUID, TransactionUID transactionUID) {
        this.dataStorage = dataStorage;
        this.name = name;
        this.journalSize = journalSize;
        this.workerUID = workerUID;
        this.transactionUID = transactionUID;
    }

    @Override
    public JournalSize size() {
        return journalSize;
    }

    @Override
    public CompletableFuture<List<Object>> read() {
        return readFromOffset(ZERO);
    }

    @Override
    public CompletableFuture<List<Object>> readFromOffset(JournalSize offset) {
        return dataStorage.readEventsFromJournal(this, offset);
    }

    @Override
    public <T> void add(T event) {
        newEvents.add(event);
    }

    @Override
    public CompletableFuture<CommitResult> commit() {
        final CompletableFuture<CommitResult> result = dataStorage.commitEvents(this, newEvents);
        newEvents = new ArrayList<>();
        return result;
    }

    @Override
    public CompletableFuture<Boolean> rollback() {
        return dataStorage.rollbackTransaction(this);
    }
}
