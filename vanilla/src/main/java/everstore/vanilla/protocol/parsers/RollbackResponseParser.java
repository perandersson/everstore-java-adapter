package everstore.vanilla.protocol.parsers;

import everstore.vanilla.io.EndianAwareInputStream;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.MessageResponse;
import everstore.vanilla.protocol.messages.RollbackTransactionResponse;

import java.io.IOException;

public final class RollbackResponseParser implements ResponseParser {
    public final class State extends ResponseState {
        public State(MessageResponse response) {
            super(response, true);
        }
    }

    @Override
    public ResponseState parse(Header header, EndianAwareInputStream stream) throws IOException {
        final boolean success = stream.readByteAsBool();
        return new State(new RollbackTransactionResponse(success));
    }

    public static final RollbackResponseParser INSTANCE = new RollbackResponseParser();
}
