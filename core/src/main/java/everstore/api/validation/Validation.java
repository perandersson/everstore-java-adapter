package everstore.api.validation;

public final class Validation {
    public static void require(boolean check, String message) {
        if (!check)
            throw new IllegalArgumentException(message);
    }
}
