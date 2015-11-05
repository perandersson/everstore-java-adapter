package everstore.vanilla.io;

public class ByteArrayNewLineReader {
    private final byte[] bytes;
    private final int length;

    private int currentPos = 0;

    public ByteArrayNewLineReader(byte[] bytes, int length) {
        this.bytes = bytes;
        this.length = length;
    }

    /**
     * Read a line.
     *
     * @param readUntilEof
     * @return
     */
    public String readLine(boolean readUntilEof) {
        int p = currentPos;
        while (p < length) {
            if (bytes[p] == '\n') {
                p++;

                final String str = new String(bytes, currentPos, p - currentPos);
                currentPos = p;
                return str;
            }
            p++;
        }

        if (p != currentPos && readUntilEof) {
            byte lastCharacter = bytes[p - 1];

            final int endIndex;
            if (lastCharacter == '\0') endIndex = p - 1;
            else endIndex = p;

            final String str = new String(bytes, currentPos, endIndex - currentPos);
            currentPos = p;
            return str;
        } else return null;
    }

    /**
     * Retrieves any bytes left. Useful for when not reading to EOF, thus not reading the last bytes when
     * reading a line.
     *
     * @return
     */
    public byte[] bytesLeft() {
        int copyLen = length - currentPos;
        byte[] newBytes = new byte[copyLen];
        System.arraycopy(bytes, currentPos, newBytes, 0, copyLen);
        return newBytes;
    }
}
