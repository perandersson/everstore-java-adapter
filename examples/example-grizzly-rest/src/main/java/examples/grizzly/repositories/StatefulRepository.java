package examples.grizzly.repositories;

import everstore.api.Adapter;
import everstore.api.CommitResult;
import everstore.api.JournalSize;
import everstore.api.Transaction;
import rx.Observable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static rx.Observable.from;

/**
 * Implement this class if you want to make us of a stateful repository for your event-sourced model
 *
 * @param <T> Type of the state this repository is managing
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

        /**
         * @return TRUE if this state is newer then the supplied state; FALSE otherwise
         */
        public boolean isNewerThen(State rhs) {
            return journalSize.isLargerThan(rhs.journalSize);
        }
    }

    // State is not initialized
    private AtomicReference<State> state = new AtomicReference<>();

    protected StatefulRepository(Adapter adapter, String journalName) {
        this.adapter = adapter;
        this.journalName = journalName;
    }

    /**
     * Open a new transaction to this repositories journal
     *
     * @return A transaction
     */
    public Observable<Transaction> openTransaction() {
        return from(adapter.openTransaction(journalName));
    }

    protected CompletableFuture<T> findState(Transaction transaction) {
        if (state.get() == null) {
            final CompletableFuture<State> futureState = initializeState(transaction);
            return futureState.thenComposeAsync(state -> completedFuture(state.object));
        } else {
            final CompletableFuture<List<Object>> read =
                    transaction.readFromOffset(new JournalSize(getSafeState().journalSize.value));
            return readAndParseEvents(transaction, read).thenComposeAsync(state -> completedFuture(state.object));
        }
    }


    private CompletableFuture<State> initializeState(Transaction transaction) {
        final CompletableFuture<List<Object>> read = transaction.read();
        return readAndParseEvents(transaction, read);
    }

    private CompletableFuture<State> readAndParseEvents(Transaction transaction,
                                                        CompletableFuture<List<Object>> read) {
        return read.thenComposeAsync(readResult -> {
            try {
                final State newState = parseEvents(readResult, transaction.size());
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
        final State ref = getSafeState();
        if (newState.isNewerThen(ref)) {
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
     * Save the supplied events and commit the transaction
     *
     * @param events The new events
     * @return
     */
    public CompletableFuture<CommitResult> saveEvents(CompletableFuture<Transaction> transaction, Object... events) {
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
