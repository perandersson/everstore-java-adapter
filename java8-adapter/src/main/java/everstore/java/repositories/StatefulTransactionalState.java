package everstore.java.repositories;

import everstore.api.CommitResult;
import everstore.api.JournalSize;
import everstore.api.Transaction;
import everstore.java.utils.Optional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class StatefulTransactionalState<T, R extends StatefulRepository<T>> {
    private final Transaction transaction;
    private final R repository;
    private final AtomicReference<RepositoryState<T>> stateToSave = new AtomicReference<>();

    public StatefulTransactionalState(Transaction transaction, R repository) {
        this.transaction = transaction;
        this.repository = repository;
        this.stateToSave.set(repository.getSafeState());
    }

    /**
     * Retrieves the current state
     *
     * @return A potential state
     */
    protected Optional<T> getState() {
        return findEvents().map(state -> state.object);
    }

    private Optional<RepositoryState<T>> findEvents() {
        return repository.getEvents(transaction).map(events -> {
            final RepositoryState<T> oldState = getSafeState();
            if (!events.isEmpty()) {
                RepositoryState<T> newState = parseEvents(events, transaction.size(), oldState.object);
                stateToSave.set(newState);
                return newState;
            } else {
                return oldState;
            }
        });
    }

    public Optional<CommitResult> commit() {
        return Optional.of(transaction.commit()).each(cr -> {
            final RepositoryState<T> possibleState = getSafeState();
            if (possibleState != null) {
                repository.safeReplaceState(possibleState);
            }
        });
    }

    public void close() {
        Optional.of(transaction.rollback()).each(c -> {
            final RepositoryState<T> possibleState = getSafeState();
            if (possibleState != null) {
                repository.safeReplaceState(possibleState);
            }
        });
    }

    public void rollback() {
        Optional.of(transaction.rollback()).each(c -> stateToSave.set(null));
    }

    private RepositoryState<T> parseEvents(final List<Object> events, final JournalSize journalSize, final T oldState) {
        T newState = oldState;
        for (final Object event : events) {
            newState = parseEvent(newState, event);
        }
        return new RepositoryState<>(journalSize, newState);
    }

    private RepositoryState<T> getSafeState() {
        RepositoryState<T> ref = stateToSave.get();
        if (ref == null)
            ref = new RepositoryState<>(JournalSize.ZERO, null);
        return ref;
    }

    /**
     * Save the supplied events and commit the transaction
     *
     * @param events The new events
     * @return
     */
    public Optional<CommitResult> saveEvents(List<Object> events) {
        events.forEach(transaction::add);
        return commit();
    }

    /**
     * Save the supplied events and commit the transaction
     *
     * @param events The new events
     * @return
     */
    public Optional<CommitResult> saveEvents(Object... events) {
        return saveEvents(Arrays.asList(events));
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
