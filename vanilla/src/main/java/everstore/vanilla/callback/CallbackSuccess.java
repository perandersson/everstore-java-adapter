package everstore.vanilla.callback;

import everstore.vanilla.protocol.DataStoreResponse;

@FunctionalInterface
public interface CallbackSuccess {
    void success(DataStoreResponse response);
}
