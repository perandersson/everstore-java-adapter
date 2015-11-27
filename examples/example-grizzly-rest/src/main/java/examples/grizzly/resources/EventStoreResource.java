package examples.grizzly.resources;

import rx.Observable;

import javax.ws.rs.container.AsyncResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.Response.status;
import static rx.Observable.from;

public interface EventStoreResource {
    /**
     * Hides the boiler-plate code for managing GET requests. If the future result is null then the get will
     * return 404 (Not found), which is part of the standard. It will also return a 500 (Internal server error) if
     * an unhandled exception occurs.
     *
     * @param response
     * @param future
     * @param fn
     * @param <T>
     * @param <U>
     */
    default <T, U> void handleGet(final AsyncResponse response, final CompletableFuture<T> future,
                                  final Function<? super T, ? extends U> fn) {
        from(future).map(result -> {
            if (result == null) return status(NOT_FOUND).build();
            else return fn.apply(result);
        }).subscribe(response::resume, e -> response.resume(status(INTERNAL_SERVER_ERROR).entity(e).build()));
    }

    /**
     * Hides the boiler-plate code for managing GET requests. If the future result is null then the get will
     * return 404 (Not found), which is part of the standard. It will also return a 500 (Internal server error) if
     * an unhandled exception occurs.
     *
     * @param response
     * @param future
     * @param <T>
     * @param <U>
     */
    default <T, U> void handleGet(final AsyncResponse response, final CompletableFuture<T> future) {
        from(future).map(result -> {
            if (result == null) return status(NOT_FOUND).build();
            else return result;
        }).subscribe(response::resume, e -> response.resume(status(INTERNAL_SERVER_ERROR).entity(e).build()));
    }

    default <T> void handleResponse(final AsyncResponse response, final Observable<T> o) {
        o.subscribe(response::resume, t -> {
            if (t instanceof HttpStatusException) {
                final HttpStatusException e = (HttpStatusException) t;
                response.resume(status(e.status).entity(e.getMessage()).build());
            }
            response.resume(status(INTERNAL_SERVER_ERROR).entity(t).build());
        });
    }

    /**
     * Hides the boiler-plate code for managing POST requests. If the supplied future returns a valid item
     * them return a conflict status code.
     *
     * @param response
     * @param future
     * @param fn
     * @param <T>
     * @param <U>
     */
    default <T, U> void handlePost(final AsyncResponse response, final CompletableFuture<T> future,
                                   final Supplier<? extends U> fn) {
        from(future).map(result -> {
            if (result != null) return status(CONFLICT).build();
            else return fn.get();
        }).subscribe(response::resume, e -> response.resume(status(INTERNAL_SERVER_ERROR).entity(e).build()));
    }

}
