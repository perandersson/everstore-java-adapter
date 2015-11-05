package everstore.vanilla.protocol.parsers;

import everstore.api.EverstoreException;
import everstore.vanilla.io.EndianAwareInputStream;
import everstore.vanilla.protocol.Header;

import java.io.IOException;

public class ErrorResponseParser implements ResponseParser {

    @Override
    public ResponseState parse(Header header, EndianAwareInputStream stream) throws IOException {
        final int errorId = stream.readInt();
        throw new EverstoreException(errorToString(errorId));

    }

    private static String errorToString(int errorId) {
        switch (errorId) {
            case 0: return "No error occurred.";
            case 1: return "Unknown error occurred.";
            case 2: return "Authentication failed.";
            case 3: return "Supplied worker does not exist.";
            case 4: return "Supplied journal is closed.";
            case 5: return "Error occurred when reading journal data.";
            case 6: return "The transaction associated with the current journal does not exist";
            case 7: return "Conflict occurred when the transaction was commited";
            case 8: return "The supplied journal path is invalid";
            default: return "Unknown error occurred.";
        }
    }

    public static final ErrorResponseParser INSTANCE = new ErrorResponseParser();
}
