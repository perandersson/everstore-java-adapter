package everstore.vanilla.protocol;

import everstore.vanilla.io.EndianAwareOutputStream;

import java.io.IOException;

public interface MessageRequest {
    /**
     * Write this message to the supplied stream
     *
     * @param stream
     * @throws IOException
     */
    void write(EndianAwareOutputStream stream) throws IOException;

    /**
     * Retrieves the size, in bytes, of this request.
     */
    int size();
}
