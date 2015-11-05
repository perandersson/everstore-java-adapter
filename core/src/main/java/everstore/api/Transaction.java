package everstore.api;

import java.util.List;
import java.util.concurrent.Future;

public interface Transaction {

    JournalSize size();

    Future<List<Object>> read();

    Future<List<Object>> readFromOffset(Offset offset);

    /**
     * Insert an event of any type to this journal
     *
     * @param event The event we want to insert into this journal
     * @param <T>
     */
    <T> void add(T event);

    Future<CommitResult> commit();

    Future<Boolean> rollback();
}
