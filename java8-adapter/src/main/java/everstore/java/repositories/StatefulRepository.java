package everstore.java.repositories;

import everstore.api.Adapter;
import everstore.api.JournalSize;
import everstore.api.Transaction;
import everstore.java.utils.Optional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static everstore.java.utils.Optional.of;

public class StatefulRepository<T> {
    protected final Adapter adapter;
    public final String journalName;
    private final AtomicReference<RepositoryState<T>> currentState = new AtomicReference<>();

    public StatefulRepository(final Adapter adapter, final String journalName) {
        this.adapter = adapter;
        this.journalName = journalName;
    }

    /**
     * Opens a transaction for this journal
     *
     * @return A potential transaction.
     */
    public Optional<Transaction> openTransaction() {
        return of(adapter.openTransaction(journalName));
    }

    /**
     * Retrieves the events associated with the supplied transaction.
     *
     * @param transaction Transaction we want to use to load events from
     * @return A list all new events associated with the supplied transaction
     */
    protected Optional<List<Object>> getEvents(final Transaction transaction) {
        final JournalSize offset = getSafeState().offset;
        final CompletableFuture<List<Object>> futureEvents = transaction.readFromOffset(offset);
        return of(futureEvents);
    }

    protected synchronized RepositoryState<T> safeReplaceState(RepositoryState<T> newState) {
        final RepositoryState<T> oldState = getSafeState();
        if (newState.isNewerThen(oldState)) {
            currentState.set(newState);
            return newState;
        }
        return oldState;
    }

    protected RepositoryState<T> getSafeState() {
        RepositoryState<T> ref = currentState.get();
        if (ref == null)
            ref = new RepositoryState<>(JournalSize.ZERO, null);
        return ref;
    }

}
