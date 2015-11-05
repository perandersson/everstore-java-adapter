package everstore.vanilla.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class BigEndianAwareOutputStream implements EndianAwareOutputStream {
    private final ByteArrayOutputStream bba = new ByteArrayOutputStream(1024);
    private final OutputStream stream;

    public BigEndianAwareOutputStream(OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void putInt(int value) throws IOException {
        bba.write((value >>> 24) & 0xFF);
        bba.write((value >>> 16) & 0xFF);
        bba.write((value >>> 8) & 0xFF);
        bba.write((value) & 0xFF);
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
