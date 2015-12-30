package everstore.vanilla.callback;

import everstore.vanilla.protocol.DataStoreResponse;

public final class RequestResponseCallback {
    public final CallbackSuccess success;
    public final CallbackFailed failure;

    public RequestResponseCallback(CallbackSuccess success, CallbackFailed failure) {
        this.success = success;
        this.failure = failure;
    }

    public void fail(Exception exception) {
        failure.failed(exception);
    }

    public void succeed(DataStoreResponse response) {
        success.success(response);
    }
}
