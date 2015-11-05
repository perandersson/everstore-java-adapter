package everstore.vanilla.protocol;

import everstore.vanilla.io.EndianAwareOutputStream;

import java.io.IOException;

public final class DataStoreRequest {
    public final Header header;
    public final MessageRequest body;

    public DataStoreRequest(Header header, MessageRequest request) {
        this.header = header;
        this.body = request;
    }

    public void write(EndianAwareOutputStream stream) throws IOException {
        header.write(stream);
        body.write(stream);
    }
}
