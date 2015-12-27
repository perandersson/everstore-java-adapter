package examples.grizzly.rest;

public class InternalServerErrorResponse extends ServerErrorResponse {

    public InternalServerErrorResponse(final String message) {
        super(500, message);
    }
}
