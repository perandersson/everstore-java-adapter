package examples.console.example2.repositories;

import everstore.api.Adapter;
import examples.console.example2.events.NameUpdated;
import examples.console.example2.events.UserCreated;
import examples.console.example2.models.User;
import examples.console.example2.models.UserId;

import java.util.concurrent.CompletableFuture;

public class UserStatefulRepository extends StatefulRepository<User> {

    public UserStatefulRepository(Adapter adapter, UserId userId) {
        super(adapter, "/java/example2/user-" + userId.value);
    }

    /**
     * Retrieve the user managed by this repository
     *
     * @return
     */
    public CompletableFuture<User> findUser() {
        return findState();
    }

    @Override
    protected User parseEvent(User state, Object event) {
        if (event instanceof UserCreated) {
            final UserCreated uc = (UserCreated) event;
            return new User(new UserId(uc.userId), uc.firstName + " " + uc.lastName);
        } else if (event instanceof NameUpdated) {
            final NameUpdated nu = (NameUpdated) event;
            return state.setName(nu.firstName + " " + nu.lastName);
        }

        throw new IllegalArgumentException("Supplied event: " + event + " is not handled");
    }
}
