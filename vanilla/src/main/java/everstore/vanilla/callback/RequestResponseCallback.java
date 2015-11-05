package everstore.vanilla.callback;

import everstore.vanilla.protocol.DataStoreResponse;

public final class RequestResponseCallback {
    public final CallbackSuccess success;
    public final CallbackFailed failure;

    public RequestResponseCallback(CallbackSuccess success, CallbackFailed failure) {
        this.success = success;
        this.failure = failure;
    }

    public void fail() {
        failure.failed();
    }

    public void succeed(DataStoreResponse response) {
        success.success(response);
    }
}
