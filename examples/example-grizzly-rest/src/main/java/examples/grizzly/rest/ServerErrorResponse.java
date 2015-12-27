package examples.grizzly.rest;

public abstract class ServerErrorResponse {
    public final int errorCode;
    public final String errorMessage;

    protected ServerErrorResponse(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
