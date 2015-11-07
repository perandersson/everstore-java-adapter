package examples.console.example2;

import everstore.api.Adapter;
import everstore.api.AdapterConfig;
import everstore.api.CommitResult;
import everstore.api.Transaction;
import everstore.api.snapshot.EventsSnapshotConfig;
import everstore.vanilla.VanillaDataStorageFactory;
import examples.console.example2.events.UserCreated;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static everstore.java.serialization.Serializers.defaultSerializer;
import static everstore.java.snapshot.events.SnapshotManagers.defaultEventsFactory;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class App {

    private final Adapter adapter;

    private App(Adapter adapter) {
        this.adapter = adapter;
    }

    public static void main(String[] args) throws IOException {
        // Configure the adapter
        final Path rootPath = Paths.get("target");
        final EventsSnapshotConfig snapshotConfig = new EventsSnapshotConfig(rootPath,
                defaultEventsFactory());

        final AdapterConfig config = new AdapterConfig("admin", "passwd",
                "localhost", (short) 6929, 6, 2000, 65526,
                defaultSerializer(), new VanillaDataStorageFactory(),
                empty(), of(snapshotConfig));

        // Connect to the server
        final Adapter adapter = new Adapter(config);
        adapter.connect();

        // Run the example application
        new App(adapter).run();

        // Close the connection
        adapter.close();
    }

    private void run() {
        // Setup a user - just so that we have some data for our example
        setupUser(new UserId(123));

        // Try to get a user
        final CompletableFuture<User> futureUser = findUser(new UserId(123));

        try {
            final User user = futureUser.get();
            System.out.println(user);
        } catch (ExecutionException e) {
            // Exception thrown if future completed exceptionally
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private CompletableFuture<User> findUser(UserId userId) {
        final CompletableFuture<Transaction> transaction =
                adapter.openTransaction("/java/example2/user-" + userId.value);
        return transaction.thenCompose(Transaction::read)
                .thenApply(this::loadUserFromEvents);
    }

    private User loadUserFromEvents(List<Object> events) {
        User user = null;
        for (Object event : events) {
            if (event instanceof UserCreated) {
                final UserCreated uc = (UserCreated) event;
                user = new User(new UserId(uc.userId), uc.firstName + " " + uc.lastName);
            }
        }
        return user;
    }

    private void setupUser(UserId userId) {
        final CompletableFuture<Transaction> transaction =
                adapter.openTransaction("/java/example2/user-" + userId.value);
        final CompletableFuture<CommitResult> result = transaction.thenCompose(t -> {
            t.add(new UserCreated(userId.value, "Per", "Andersson"));
            return t.commit();
        });

        try {
            CompletableFuture.allOf(result).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
