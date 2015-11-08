package everstore.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Transaction {

    JournalSize size();

    CompletableFuture<List<Object>> read();

    CompletableFuture<List<Object>> readFromOffset(JournalSize offset);

    /**
     * Insert an event of any type to this journal
     *
     * @param event The event we want to insert into this journal
     * @param <T>
     */
    <T> void add(T event);

    CompletableFuture<CommitResult> commit();

    CompletableFuture<Boolean> rollback();
}
