package everstore.vanilla.protocol;

import everstore.vanilla.HeaderProperties;
import everstore.vanilla.RequestUID;
import everstore.vanilla.WorkerUID;
import everstore.vanilla.io.EndianAwareInputStream;
import everstore.vanilla.io.EndianAwareOutputStream;

import java.io.IOException;

public final class Header {
    public final RequestType type;
    public final int size;
    public final RequestUID requestUID;
    public final HeaderProperties properties;
    public final WorkerUID workerUID;

    public Header(RequestType type, int size, RequestUID requestUID, HeaderProperties properties,
                  WorkerUID workerUID) {
        this.type = type;
        this.size = size;
        this.requestUID = requestUID;
        this.properties = properties;
        this.workerUID = workerUID;
    }

    public void write(EndianAwareOutputStream stream) throws IOException {
        stream.putInt(type.id);
        stream.putInt(size);
        stream.putInt(requestUID.value);
        stream.putInt(properties.value);
        stream.putInt(workerUID.value);
    }

    @Override
    public String toString() {
        return "Header{" +
                "type=" + type +
                ", size=" + size +
                ", requestUID=" + requestUID +
                ", properties=" + properties +
                ", workerUID=" + workerUID +
                '}';
    }

    /**
     * Check to see if this message is split into multiple parts.
     */
    public boolean isMultipart() {
        return (properties.value & HeaderProperties.MULTIPART) != 0;
    }

    /**
     * Check to see if this message is compressed or not.
     */
    public boolean isCompressed() {
        return (properties.value & HeaderProperties.COMPRESSED) != 0;
    }

    /**
     * Read the header from the supplied stream
     *
     * @param stream
     * @return
     */
    public static Header read(final EndianAwareInputStream stream) throws IOException {
        return new Header(
                RequestType.fromId(stream.readInt()),
                stream.readInt(),
                new RequestUID(stream.readInt()),
                new HeaderProperties(stream.readInt()),
                new WorkerUID(stream.readInt()));
    }
}
