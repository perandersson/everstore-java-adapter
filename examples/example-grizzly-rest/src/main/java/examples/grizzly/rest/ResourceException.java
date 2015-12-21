package examples.grizzly.rest;

import javax.ws.rs.core.Response;

public class ResourceException extends RuntimeException {
    public final Response.Status status;

    public ResourceException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }
}
