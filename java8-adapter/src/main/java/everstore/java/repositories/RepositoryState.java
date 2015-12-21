package everstore.java.repositories;

import everstore.api.JournalSize;

/**
 * Object representing a state at a specific offset of a journal.
 *
 * @param <T> The state type
 */
public final class RepositoryState<T> {
    public final JournalSize offset;
    public final T object;

    public RepositoryState(final JournalSize offset, final T object) {
        this.offset = offset;
        this.object = object;
    }

    /**
     * Check to see if this state is newer then the supplied one
     *
     * @param rhs The state we want to compare with
     * @return TRUE if this state is newer; FALSE otherwise.
     */
    public boolean isNewerThen(RepositoryState<T> rhs) {
        return offset.isLargerThan(rhs.offset);
    }
}
