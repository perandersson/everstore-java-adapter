package examples.grizzly.repositories;

import everstore.api.Transaction;

public class StateFulTransaction {
    public final Transaction transaction;

    public StateFulTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void close() {
        // TODO: Make a method called "close"
        transaction.rollback();
    }
}
