package everstore.vanilla.callback;

import everstore.vanilla.protocol.DataStoreRequest;
import everstore.vanilla.protocol.DataStoreResponse;

public final class RequestResponseCallback {
    public final CallbackSuccess success;
    public final CallbackFailed failure;
    public final DataStoreRequest request;

    public RequestResponseCallback(CallbackSuccess success, CallbackFailed failure, DataStoreRequest request) {
        this.success = success;
        this.failure = failure;
        this.request = request;
    }

    public void fail(Exception exception) {
        failure.failed(exception);
    }

    public void succeed(DataStoreResponse response) {
        success.success(response);
    }
}
