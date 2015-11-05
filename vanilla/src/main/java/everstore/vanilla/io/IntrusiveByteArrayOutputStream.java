package everstore.vanilla.io;

import java.util.Arrays;

public final class IntrusiveByteArrayOutputStream {
    public byte[] buffer = new byte[32768];
    public int count = 0;

    /**
     * Ensure that the intrusive byte array can handle the upcomming bytes
     *
     * @param capacity
     */
    public void ensureCapacity(int capacity) {
        if (capacity > buffer.length) {
            buffer = Arrays.copyOf(buffer, capacity);
        }
    }

    public void write(byte[] b, int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }

        ensureCapacity(count + len);
        System.arraycopy(b, off, buffer, count, len);
        count += len;
    }

    public void insert(byte[] bytes) {
        ensureCapacity(count + bytes.length);
        System.arraycopy(bytes, 0, buffer, count, bytes.length);
        count += bytes.length;
    }

    public void reset() {
        count = 0;
    }
}
