package everstore.vanilla.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class LittleEndianAwareOutputStream implements EndianAwareOutputStream {
    private final ByteArrayOutputStream bba = new ByteArrayOutputStream(1024);
    private final OutputStream stream;

    public LittleEndianAwareOutputStream(OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void putInt(int value) throws IOException {
        bba.write(value);
        bba.write(value >> 8);
        bba.write(value >> 16);
        bba.write(value >> 24);
    }

    @Override
    public void putString(String value) throws IOException {
        bba.write(value.getBytes());
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void flush() throws IOException {
        bba.writeTo(stream);
        stream.flush();
        bba.reset();
    }
}
