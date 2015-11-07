package examples.console.example1;

import everstore.api.Adapter;
import everstore.api.AdapterConfig;
import everstore.api.CommitResult;
import everstore.api.Transaction;
import everstore.java.serialization.jackson.JacksonSerializer;
import everstore.vanilla.VanillaDataStorageFactory;

import java.util.concurrent.CompletableFuture;

public class App {
    public static void main(String[] args) throws Exception {
        // Adapter configuration
        final AdapterConfig config = new AdapterConfig("admin", "passwd",
                "localhost", (short) 6929, 6, new JacksonSerializer(),
                new VanillaDataStorageFactory());

        // Connect to the server
        final Adapter adapter = new Adapter(config);
        adapter.connect();

        // Open two transactions to the same journal
        CompletableFuture<Transaction> transaction1 = adapter.openTransaction("/java/user/per.andersson@funnic.com");
        CompletableFuture<Transaction> transaction2 = adapter.openTransaction("/java/user/per.andersson@funnic.com");

        // Try to save two events of the same type
        CompletableFuture<CommitResult> events1 = transaction1.thenCompose(t -> {
            t.add(new UserCreated("per.andersson@funnic.com"));
            return t.commit();
        });

        CompletableFuture<CommitResult> events2 = transaction2.thenCompose(t -> {
            t.add(new UserCreated("dim.raven@gmail.com"));
            return t.commit();
        });

        // Wait for the futures to be complete
        CompletableFuture.allOf(events1, events2);

        // Print out the result
        if (events1.get().success)
            System.out.println("transaction1 added num events: " + events1.get().events.size());
        else
            System.out.println("transaction1 failed to add num events: " + events1.get().events.size());

        if (events2.get().success)
            System.out.println("transaction2 added num events: " + events2.get().events.size());
        else
            System.out.println("transaction2 failed to add num events: " + events2.get().events.size());

        // Close the connection
        adapter.close();
    }
}
