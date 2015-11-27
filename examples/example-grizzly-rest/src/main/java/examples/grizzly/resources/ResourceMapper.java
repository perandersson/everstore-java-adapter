package examples.grizzly.resources;

import everstore.api.CommitResult;

import java.util.function.Supplier;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public final class ResourceMapper {
    /**
     * A conflict has occurred. This usually happens if we are trying to POST a value that already exists.
     *
     * @param conflicts
     * @param message The message we want to return to the user
     */
    public static void conflict(boolean conflicts, Supplier<String> message) {
        if (conflicts)
            throw new HttpStatusException(CONFLICT, message.get());
    }

    /**
     * If the expression is evaluated as true, then the notify the user that the request resource
     * was not found.
     *
     * @param expression
     * @param message The message we want to return to the user
     */
    public static void notFound(boolean expression, Supplier<String> message) {
        if (expression)
            throw new HttpStatusException(NOT_FOUND, message.get());
    }


    /**
     * If the supplied commit is marked as "unsuccessful" - i.e. it failed, then make sure to return a
     * "CONFLICT" http response.
     *
     * TODO: Add support for a retry mechanism
     *
     * @param commitResult The commit result
     * @param message The message if the commit failed
     */
    public static void commitFailed(CommitResult commitResult, Supplier<String> message) {
        if (!commitResult.success)
            throw new HttpStatusException(CONFLICT, message.get());
    }
}
