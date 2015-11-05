package everstore.vanilla.protocol;

public final class DataStoreResponse {
    public final Header header;
    public final MessageResponse response;

    public DataStoreResponse(Header header, MessageResponse response) {
        this.header = header;
        this.response = response;
    }
}
