package examples.console.example2;

import everstore.api.Adapter;
import everstore.api.AdapterConfig;
import everstore.api.CommitResult;
import everstore.api.snapshot.EventsSnapshotConfig;
import everstore.vanilla.VanillaDataStorageFactory;
import examples.console.example2.events.NameUpdated;
import examples.console.example2.events.UserCreated;
import examples.console.example2.models.User;
import examples.console.example2.models.UserId;
import examples.console.example2.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static everstore.java.serialization.Serializers.defaultSerializer;
import static everstore.java.snapshot.events.SnapshotManagers.defaultFactory;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;

public class App {

    /**
     * Entry-point for the application
     */
    public static void main(String[] args) throws IOException {
        // Configure the adapter
        final Path rootPath = Paths.get("target");
        final EventsSnapshotConfig snapshotConfig = new EventsSnapshotConfig(rootPath, defaultFactory());

        final AdapterConfig config = new AdapterConfig("admin", "passwd",
                "localhost", (short) 6929, 6, 2000, 65526,
                defaultSerializer(), new VanillaDataStorageFactory(),
                of(snapshotConfig));

        // Connect to the server
        final Adapter adapter = new Adapter(config);
        adapter.connect();

        // Run the example application
        new App(adapter).run();

        // Close the connection
        adapter.close();
    }

    private final Adapter adapter;
    private final UserRepository userRepository;

    private App(Adapter adapter) {
        this.adapter = adapter;
        this.userRepository = new UserRepository(adapter, 1000);
    }

    private void run() {
        final UserId userId = new UserId(123);
        try {
            // Setup a user - just so that we have some data for our example
            setupUser(userId);

            // Get the user and display the result
            findAndDisplayUser(userId);

            // Add new events
            CompletableFuture<CommitResult> commitResult =
                    userRepository.saveEvents(userId, singletonList(new NameUpdated("John", "Doe")));
            try {
                commitResult.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Get the user again, but with new events available
            findAndDisplayUser(userId);

        } catch (ExecutionException e) {
            // Exception thrown if future completed exceptionally
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void findAndDisplayUser(UserId userId) throws InterruptedException, ExecutionException {
        final CompletableFuture<User> futureUser = userRepository.findUser(userId);
        final User user = futureUser.get();
        System.out.println(user);
    }

    /**
     * Setup test data used by this example
     *
     * @param userId
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void setupUser(UserId userId) throws ExecutionException, InterruptedException {
        final String name = "/java/example2/user-" + userId.value;

        final CompletableFuture<Boolean> journalExists = adapter.journalExists(name);
        if (!journalExists.get()) {
            final CompletableFuture<CommitResult> result = userRepository.saveEvents(userId,
                    singletonList(new UserCreated(userId.value, "Per", "Andersson")));

            try {
                result.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
