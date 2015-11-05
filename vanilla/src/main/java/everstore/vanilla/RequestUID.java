package everstore.vanilla;

public final class RequestUID {
    public final int value;

    public RequestUID(int value) {
        this.value = value;
    }

    public static final RequestUID ZERO = new RequestUID(0);
}
