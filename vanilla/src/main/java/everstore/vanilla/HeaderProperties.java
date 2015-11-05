package everstore.vanilla;

public final class HeaderProperties {

    /**
     * Indicates that the message is split up into multiple smaller parts
     */
    public static final int MULTIPART = 1;

    /**
     * Indicates that the request is compressed.
     * TODO: Add support for this
     */
    public static final int COMPRESSED = 2;

    /**
     * Indicates that the data includes timestamp for each event row
     */
    public static final int INCLUDE_TIMESTAMP = 4;

    public static final HeaderProperties NONE = new HeaderProperties(0);

    public final int value;

    public HeaderProperties(int value) {
        this.value = value;
    }
}
