package everstore.vanilla.callback;

@FunctionalInterface
public interface CallbackFailed {
    void failed(Exception exception);
}
