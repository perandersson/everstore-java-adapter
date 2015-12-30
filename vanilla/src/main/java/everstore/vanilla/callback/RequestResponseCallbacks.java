package everstore.vanilla.callback;

import everstore.vanilla.RequestUID;
import everstore.vanilla.protocol.DataStoreRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentSkipListMap;

public class RequestResponseCallbacks {
    private final ConcurrentSkipListMap<Integer, RequestResponseCallback> callbacks = new ConcurrentSkipListMap<>();

    public RequestResponseCallback removeAndGet(RequestUID requestUID) {
        return callbacks.remove(requestUID.value);
    }

    public List<RequestResponseCallback> removeAll() {
        final List<RequestResponseCallback> result = new ArrayList<>();
        Integer key;
        try {
            while ((key = callbacks.lastKey()) != null) {
                result.add(callbacks.remove(key));
            }
        } catch (NoSuchElementException ignored) {
        }
        return result;
    }

    public void add(RequestUID requestUID, CallbackSuccess success, CallbackFailed failure, DataStoreRequest request) {
        callbacks.put(requestUID.value, new RequestResponseCallback(success, failure, request));
    }
}
