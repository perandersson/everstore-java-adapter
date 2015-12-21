package examples.console.example2.repositories;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import everstore.api.Adapter;
import everstore.api.CommitResult;
import examples.console.example2.models.User;
import examples.console.example2.models.UserId;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserRepository {
    private final Adapter adapter;
    private final Cache<UserId, UserStatefulRepository> repositories;

    public UserRepository(Adapter adapter, int maxStates) {
        this.adapter = adapter;
        this.repositories = Caffeine.newBuilder()
                .maximumSize(maxStates).<UserId, UserStatefulRepository>build();
    }

    public CompletableFuture<User> findUser(final UserId userId) {
        UserStatefulRepository repository =
                repositories.get(userId, id -> new UserStatefulRepository(adapter, id));
        return repository.findUser();
    }

    public CompletableFuture<CommitResult> saveEvents(final UserId userId, List<Object> events) {
        UserStatefulRepository repository =
                repositories.get(userId, id -> new UserStatefulRepository(adapter, id));
        return repository.saveEvents(events);
    }
}
