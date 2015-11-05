package everstore.vanilla.protocol.parsers;

import everstore.vanilla.io.EndianAwareInputStream;
import everstore.vanilla.protocol.Header;

import java.io.IOException;

public interface ResponseParser {
    default ResponseParser create(ResponseState state) {
        return this;
    }

    ResponseState parse(Header header, EndianAwareInputStream stream) throws IOException;
}
