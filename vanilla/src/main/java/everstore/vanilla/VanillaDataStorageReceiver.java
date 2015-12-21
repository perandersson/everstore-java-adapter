package everstore.vanilla;

import everstore.vanilla.callback.RequestResponseCallback;
import everstore.vanilla.callback.RequestResponseCallbacks;
import everstore.vanilla.io.EndianAwareInputStream;
import everstore.vanilla.io.IntrusiveByteArrayOutputStream;
import everstore.vanilla.protocol.DataStoreResponse;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.parsers.CommitResponseParser;
import everstore.vanilla.protocol.parsers.ErrorResponseParser;
import everstore.vanilla.protocol.parsers.JournalExistsResponseParser;
import everstore.vanilla.protocol.parsers.NewTransactionResponseParser;
import everstore.vanilla.protocol.parsers.ReadJournalResponseParser;
import everstore.vanilla.protocol.parsers.ResponseParser;
import everstore.vanilla.protocol.parsers.ResponseState;
import everstore.vanilla.protocol.parsers.RollbackResponseParser;
import everstore.vanilla.protocol.parsers.UnknownResponseParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static everstore.api.validation.Validation.require;

public class VanillaDataStorageReceiver implements Runnable {

    private final Thread thread;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final EndianAwareInputStream inputStream;
    private final RequestResponseCallbacks callbacks;
    private final IntrusiveByteArrayOutputStream byteStream = new IntrusiveByteArrayOutputStream();

    private Map<Integer, ResponseParser> responseParsers = new HashMap<>();

    public VanillaDataStorageReceiver(final String name, final EndianAwareInputStream inputStream,
                                      final RequestResponseCallbacks callbacks) {
        require(name.length() > 0, "The name for a VanillaDataStorageReceiver must be set");
        require(callbacks != null, "A callbacks container is required for this receiver to work");

        thread = new Thread(this, "VanillaDataStorageReceiver_" + name);
        thread.setUncaughtExceptionHandler((t, e) -> uncaughtException(e));

        this.inputStream = inputStream;
        this.callbacks = callbacks;
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                byteStream.reset();
                final Header header = Header.read(inputStream);
                final ResponseParser parser = getResponseParser(header);
                try {
                    final ResponseState state = parser.parse(header, inputStream);
                    if (state.complete) {
                        final RequestResponseCallback callback = callbacks.removeAndGet(header.requestUID);
                        callback.succeed(new DataStoreResponse(header, state.response));
                    } else {
                        responseParsers.put(header.requestUID.value, parser.create(state));
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // TODO: Add better logging.
                    final RequestResponseCallback callback = callbacks.removeAndGet(header.requestUID);
                    callback.fail();
                }
            }
        } catch (Exception e) {
            // Let uncaughtException method handle the error
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve a usable response parser based on the supplied header
     *
     * @param header Message header
     * @return A response parser. This always return a non-null value
     */
    private ResponseParser getResponseParser(final Header header) {
        final ResponseParser parser = responseParsers.remove(header.requestUID.value);
        if (parser != null) return parser;
        return createResponseParser(header);
    }

    /**
     * Create a parser based on the supplied header
     *
     * @param header Message header
     * @return A response parser
     */
    private ResponseParser createResponseParser(final Header header) {
        switch (header.type) {
            case NEW_TRANSACTION:
                return NewTransactionResponseParser.INSTANCE;
            case COMMIT_TRANSACTION:
                return CommitResponseParser.INSTANCE;
            case READ_JOURNAL:
                return new ReadJournalResponseParser(null, false, byteStream, new byte[0]);
            case ROLLBACK_TRANSACTION:
                return RollbackResponseParser.INSTANCE;
            case JOURNAL_EXISTS:
                return JournalExistsResponseParser.INSTANCE;
            case ERROR:
                return ErrorResponseParser.INSTANCE;
            default:
                return UnknownResponseParser.INSTANCE;
        }
    }

    public void close() {
        running.set(false);
        try {
            inputStream.close();
        } catch (IOException e) {
            // TODO: Add logging, but ignore otherwise
        }
    }

    public void start() {
        thread.start();
    }

    private void uncaughtException(Throwable e) {
        if (running.get()) {
            e.printStackTrace();
        }
        close();
    }
}
