package everstore.vanilla.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class LittleEndianAwareInputStream implements EndianAwareInputStream {
    private final InputStream stream;

    public LittleEndianAwareInputStream(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public int readInt() throws IOException {
        int ch1 = stream.read();
        int ch2 = stream.read();
        int ch3 = stream.read();
        int ch4 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) throw new EOFException();

        return ((ch4 & 0xff) << 24) | ((ch3 & 0xff) << 16) | ((ch2 & 0xff) << 8) | (ch1 & 0xff);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public boolean readByteAsBool() throws IOException {
        int b = stream.read();
        if (b < 0) throw new EOFException();
        return b == 1;
    }

    @Override
    public void read(int length, IntrusiveByteArrayOutputStream byteStream) throws IOException {
        byteStream.ensureCapacity(byteStream.count + length);

        int n = stream.read(byteStream.buffer, byteStream.count, length);
        if (n == -1) throw new IOException("Could not read data from stream");

        while (n < length) {
            int t = stream.read(byteStream.buffer, byteStream.count + n, length - n);
            if (t == -1) throw new IOException("Could not read data from stream");
            n += t;
        }

        // TODO: *puke*
        byteStream.count += n;
    }
}
