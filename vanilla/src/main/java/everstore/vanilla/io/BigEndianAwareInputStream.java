package everstore.vanilla.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class BigEndianAwareInputStream implements EndianAwareInputStream {
    private final InputStream stream;
    private final DataInputStream dataInputStream;

    public BigEndianAwareInputStream(InputStream stream) {
        this.stream = stream;
        this.dataInputStream = new DataInputStream(stream);
    }

    @Override
    public int readInt() throws IOException {
        return dataInputStream.readInt();
    }

    @Override
    public boolean readByteAsBool() throws IOException {
        return dataInputStream.readBoolean();
    }

    @Override
    public void close() throws IOException {
        stream.close();
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
