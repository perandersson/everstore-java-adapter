package everstore.vanilla.protocol.parsers;

import everstore.api.JournalSize;
import everstore.vanilla.io.EndianAwareInputStream;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.MessageResponse;
import everstore.vanilla.protocol.messages.CommitTransactionResponse;

import java.io.IOException;

public final class CommitResponseParser implements ResponseParser {

    public final class State extends ResponseState {
        public State(MessageResponse response) {
            super(response, true);
        }
    }

    @Override
    public ResponseState parse(Header header, EndianAwareInputStream stream) throws IOException {
        final boolean success = stream.readIntAsBool();
        final JournalSize size = new JournalSize(stream.readInt());
        return new State(new CommitTransactionResponse(success, size));
    }

    public static final CommitResponseParser INSTANCE = new CommitResponseParser();
}
