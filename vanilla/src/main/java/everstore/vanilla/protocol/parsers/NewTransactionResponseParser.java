package everstore.vanilla.protocol.parsers;

import everstore.api.JournalSize;
import everstore.vanilla.TransactionUID;
import everstore.vanilla.io.EndianAwareInputStream;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.MessageResponse;
import everstore.vanilla.protocol.messages.NewTransactionResponse;

import java.io.IOException;

public final class NewTransactionResponseParser implements ResponseParser {

    public final class State extends ResponseState {
        public State(MessageResponse response) {
            super(response, true);
        }
    }

    @Override
    public ResponseState parse(Header header, EndianAwareInputStream stream) throws IOException {
        final JournalSize size = new JournalSize(stream.readInt());
        final TransactionUID transactionUID = new TransactionUID(stream.readInt());
        return new State(new NewTransactionResponse(size, transactionUID));
    }

    public static final NewTransactionResponseParser INSTANCE = new NewTransactionResponseParser();
}
