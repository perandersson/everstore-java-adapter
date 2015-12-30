package everstore.vanilla;

import everstore.vanilla.io.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

public final class ServerProperties {
    public final ByteOrder byteOrder;
    public final int version;
    public final boolean authenticateRequired;

    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ServerProperties(ByteOrder byteOrder, int version, boolean authenticateRequired,
                            InputStream inputStream, OutputStream outputStream) {
        this.byteOrder = byteOrder;
        this.version = version;
        this.authenticateRequired = authenticateRequired;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public EndianAwareInputStream createInputStream() {
        if (byteOrder == LITTLE_ENDIAN)
            return new LittleEndianAwareInputStream(inputStream);
        else
            return new BigEndianAwareInputStream(inputStream);
    }

    public EndianAwareOutputStream createOutputStream() {
        if (byteOrder == LITTLE_ENDIAN)
            return new LittleEndianAwareOutputStream(outputStream);
        else
            return new BigEndianAwareOutputStream(outputStream);
    }
}
