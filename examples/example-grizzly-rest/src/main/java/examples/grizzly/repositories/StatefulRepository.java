package examples.grizzly.repositories;

import everstore.api.Adapter;
import everstore.api.CommitResult;
import everstore.api.JournalSize;
import everstore.api.Transaction;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * @param <T>
 */
public abstract class StatefulRepository<T> {
    protected final Adapter adapter;
    private final String journalName;

    private final class State {
        public final T object;
        public final JournalSize journalSize;

        private State(T object, JournalSize journalSize) {
            this.object = object;
            this.journalSize = journalSize;
        }
    }

    // State is not initialized
    private AtomicReference<State> state = new AtomicReference<>();

    protected StatefulRepository(Adapter adapter, String journalName) {
        this.adapter = adapter;
        this.journalName = journalName;
    }

    protected CompletableFuture<Transaction> openTransaction() {
        return adapter.openTransaction(journalName);
    }

    protected CompletableFuture<T> findState() {
        if (state.get() == null) {
            final CompletableFuture<State> futureState = initializeState();
            return futureState.thenComposeAsync(state -> completedFuture(state.object));
        } else {
            final CompletableFuture<Transaction> transaction = openTransaction();
            final CompletableFuture<List<Object>> read =
                    transaction.thenCompose(t -> t.readFromOffset(new JournalSize(getSafeState().journalSize.value)));
            return readAndParseEvents(transaction, read).thenComposeAsync(state -> completedFuture(state.object));
        }
    }


    private CompletableFuture<State> initializeState() {
        final CompletableFuture<Transaction> transaction = openTransaction();
        final CompletableFuture<List<Object>> read = transaction.thenComposeAsync(Transaction::read);
        return readAndParseEvents(transaction, read);
    }

    private CompletableFuture<State> readAndParseEvents(CompletableFuture<Transaction> transaction,
                                                        CompletableFuture<List<Object>> read) {
        return read.thenComposeAsync(readResult -> {
            try {
                final Transaction t = transaction.get();
                final State newState = parseEvents(readResult, t.size());
                saveState(newState);
                return completedFuture(newState);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Save the new state if the new states journalSize is larger (i.e. the journal is newer)
     *
     * @param newState The new state
     */
    private synchronized void saveState(State newState) {
        State ref = getSafeState();
        if (newState.journalSize.isLargerThan(ref.journalSize)) {
            state.set(newState);
        }
    }

    private State parseEvents(final List<Object> events, final JournalSize journalSize) {
        T newState = getSafeState().object;
        for (final Object event : events) {
            newState = parseEvent(newState, event);
        }
        return new State(newState, journalSize);
    }

    private State getSafeState() {
        State ref = state.get();
        if (ref == null)
            ref = new State(null, JournalSize.ZERO);
        return ref;
    }

    /**
     * Save the supplied events and commit the transaction
     *
     * @param events The new events
     * @return
     */
    public CompletableFuture<CommitResult> saveEvents(List<Object> events) {
        final CompletableFuture<Transaction> transaction = openTransaction();
        return transaction.thenComposeAsync(t -> {
            events.forEach(t::add);
            return t.commit();
        });
    }

    /**
     * Save the supplied events and commit the transaction
     *
     * @param events The new events
     * @return
     */
    public CompletableFuture<CommitResult> saveEvents(Object... events) {
        return saveEvents(singletonList(events));
    }

    /**
     * Creates a new state, based on the previous state and the supplied event.
     *
     * @param state The current state
     * @param event The event we want to use to parse update the state
     * @return A new state
     */
    protected abstract T parseEvent(T state, Object event);

}
