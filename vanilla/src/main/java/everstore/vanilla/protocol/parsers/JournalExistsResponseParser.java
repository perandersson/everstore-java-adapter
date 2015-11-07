package everstore.vanilla.protocol.parsers;

import everstore.vanilla.io.EndianAwareInputStream;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.MessageResponse;
import everstore.vanilla.protocol.messages.JournalExistsResponse;

import java.io.IOException;

public final class JournalExistsResponseParser implements ResponseParser {
    public final class State extends ResponseState {
        public State(MessageResponse response) {
            super(response, true);
        }
    }

    @Override
    public ResponseState parse(Header header, EndianAwareInputStream stream) throws IOException {
        final boolean exists = stream.readByteAsBool();
        return new State(new JournalExistsResponse(exists));
    }

    public static final JournalExistsResponseParser INSTANCE = new JournalExistsResponseParser();
}
