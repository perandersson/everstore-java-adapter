package everstore.vanilla.protocol.parsers;

import everstore.vanilla.protocol.MessageResponse;

public class ResponseState {
    public final MessageResponse response;
    public final boolean complete;

    public ResponseState(MessageResponse response, boolean complete) {
        this.response = response;
        this.complete = complete;
    }
}
