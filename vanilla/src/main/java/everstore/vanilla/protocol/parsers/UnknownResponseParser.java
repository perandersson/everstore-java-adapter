package everstore.vanilla.protocol.parsers;

import everstore.vanilla.io.EndianAwareInputStream;
import everstore.vanilla.protocol.Header;

public class UnknownResponseParser implements ResponseParser {
    @Override
    public ResponseState parse(Header header, EndianAwareInputStream stream) {
        throw new IllegalStateException("Header " + header + " is not mapped to a valid response parser");
    }

    public static final UnknownResponseParser INSTANCE = new UnknownResponseParser();
}
