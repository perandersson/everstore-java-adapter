package everstore.vanilla.io;

import java.io.IOException;

public interface EndianAwareOutputStream {

    void putInt(int value) throws IOException;

    void putString(String value) throws IOException;

    /**
     * Close this input stream
     */
    void close() throws IOException;

    /**
     * Flush the output stream and send any cached data to the server
     *
     * @throws IOException
     */
    void flush() throws IOException;

}
