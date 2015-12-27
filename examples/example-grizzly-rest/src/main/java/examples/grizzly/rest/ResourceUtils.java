package examples.grizzly.rest;

import everstore.api.CommitResult;
import everstore.java.utils.Optional;

import javax.ws.rs.container.AsyncResponse;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.status;

public final class ResourceUtils {
    public static <T> void conflicts(T object, String message) {
        if (object != null)
            throw new ResourceException(message, CONFLICT);
    }

    public static <T> void conflicts(Optional<T> object, String message) {
        if (object.isPresent())
            throw new ResourceException(message, CONFLICT);
    }

    public static <T> void notFound(T object, String message) {
        if (object == null)
            throw new ResourceException(message, NOT_FOUND);

    }

    public static void validateCommit(CommitResult commitResult, String message) {
        if (!commitResult.success)
            throw new ResourceException(message, CONFLICT);
    }

    public static <T> void handlePost(AsyncResponse response, Optional<T> result) {
        response.setTimeout(1000, MILLISECONDS);

        result.each(r -> {
            if (r == null) {
                response.resume(status(INTERNAL_SERVER_ERROR).build());
            } else {
                response.resume(r);
            }
        }).exceptionally(e -> {
            if (e instanceof ResourceException) {
                final ResourceException re = (ResourceException) e;
                response.resume(status(re.status).entity(e).build());
            } else {
                response.resume(status(INTERNAL_SERVER_ERROR).entity(new InternalServerErrorResponse(e.getMessage())).build());
            }
            e.printStackTrace();
        });
    }

    public static <T> void handleGet(AsyncResponse response, Optional<T> result) {
        response.setTimeout(1000, MILLISECONDS);

        result.each(r -> {
            if (r == null)
                response.resume(status(NOT_FOUND).build());
            else
                response.resume(r);
        }).exceptionally(e -> {
            if (e instanceof ResourceException) {
                final ResourceException re = (ResourceException) e;
                response.resume(status(re.status).entity(e).build());
            } else {
                response.resume(status(INTERNAL_SERVER_ERROR).entity(new InternalServerErrorResponse(e.getMessage())).build());
            }
            e.printStackTrace();
        });
    }

}
