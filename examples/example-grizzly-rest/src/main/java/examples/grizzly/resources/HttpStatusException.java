package examples.grizzly.resources;

import javax.ws.rs.core.Response.Status;

public class HttpStatusException extends RuntimeException {
    public final Status status;

    public HttpStatusException(Status status, String message) {
        super(message);
        this.status = status;
    }
}
