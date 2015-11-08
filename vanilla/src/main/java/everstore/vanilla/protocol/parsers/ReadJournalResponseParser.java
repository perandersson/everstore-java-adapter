package everstore.vanilla.protocol.parsers;

import everstore.api.JournalSize;
import everstore.api.validation.Validation;
import everstore.vanilla.Event;
import everstore.vanilla.io.ByteArrayNewLineReader;
import everstore.vanilla.io.EndianAwareInputStream;
import everstore.vanilla.io.IntrusiveByteArrayOutputStream;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.MessageResponse;
import everstore.vanilla.protocol.messages.ReadJournalResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static everstore.api.validation.Validation.require;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Optional.empty;

public final class ReadJournalResponseParser implements ResponseParser {
    public final class State extends ResponseState {
        public final byte[] bytesLeft;
        public final ReadJournalResponse readJournalResponse;

        public State(MessageResponse response, boolean complete, byte[] bytesLeft) {
            super(response, complete);
            this.bytesLeft = bytesLeft;
            readJournalResponse = response != null ? (ReadJournalResponse)response : null;
        }
    }

    private final State state;
    private final IntrusiveByteArrayOutputStream byteStream;

    public ReadJournalResponseParser(MessageResponse response, boolean complete,
                                     IntrusiveByteArrayOutputStream byteStream, byte[] bytesLeft) {
        require(byteStream != null, "A byteStream is required when using this constructor");
        require(bytesLeft != null, "A bytesLeft blob is required when using this constructor");

        this.state = new State(response, complete, bytesLeft);
        this.byteStream = byteStream;
    }

    public ReadJournalResponseParser(final State state, IntrusiveByteArrayOutputStream byteStream) {
        require(state != null, "A state is required when using this constructor");
        require(byteStream != null, "A byteStream is required when using this constructor");

        this.state = state;
        this.byteStream = byteStream;
    }

    @Override
    public ResponseParser create(final ResponseState state) {
        return new ReadJournalResponseParser((State) state, byteStream);
    }

    @Override
    public ResponseState parse(final Header header, final EndianAwareInputStream stream) throws IOException {
        // Fill the byte array with the previous data blob
        byteStream.insert(state.bytesLeft);

        // The actual events data
        int numBytes = stream.readInt();
        stream.read(numBytes, byteStream);

        if (byteStream.count > 0) {
            if (header.isMultipart()) {
                return readJournal(byteStream.buffer, byteStream.count, false);
            } else {
                return readJournal(byteStream.buffer, byteStream.count, true);
            }
        } else {
            return new State(new ReadJournalResponse(new ArrayList<>()), true, new byte[0]);
        }
    }

    /**
     * Read the journal data from the response.
     *
     * @param bytes
     * @param length
     * @param eof If this response contains an eof marker - i.e. last part of the multipart message
     * @return The resulting state
     */
    private ResponseState readJournal(byte[] bytes, int length, boolean eof) {
        final ByteArrayNewLineReader reader = new ByteArrayNewLineReader(bytes, length);

        // Create a new list where we put the upcoming events.
        final List<Event> events = state.readJournalResponse != null ?
                new ArrayList<>(state.readJournalResponse.events) : new ArrayList<>();

        // Parse out each event data line
        String line;
        while ((line = reader.readLine(eof)) != null) {
            events.add(new Event(line, empty()));
        }

        // Return the resulting state
        return new State(new ReadJournalResponse(events), eof, reader.bytesLeft());
    }
}
