package everstore.vanilla.protocol.messages;

import everstore.vanilla.protocol.MessageResponse;

public final class JournalExistsResponse implements MessageResponse {
    public final boolean exists;

    public JournalExistsResponse(boolean exists) {
        this.exists = exists;
    }
}
