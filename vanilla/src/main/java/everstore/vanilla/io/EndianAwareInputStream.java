package everstore.vanilla.io;

import java.io.IOException;

public interface EndianAwareInputStream {

    /**
     * Read an integer from the input stream.
     */
    int readInt() throws IOException;

    /**
     * Close this input stream
     */
    void close() throws IOException;

    /**
     * Read an integer from the stream into and convert it into a boolean character.
     */
    default boolean readIntAsBool() throws IOException {
        return readInt() == 1;
    }

    /**
     * Read a byte from the stream into and convert it into a boolean character.
     */
    boolean readByteAsBool() throws IOException;

    void read(int length, IntrusiveByteArrayOutputStream byteStream) throws IOException;
}
