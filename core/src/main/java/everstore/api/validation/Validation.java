package everstore.api.validation;

public final class Validation {

    /**
     * Syntactic sugar for handling and throwing exceptions if a comparison fails
     *
     * @param check
     * @param message
     */
    public static void require(final boolean check, final String message) {
        if (!check)
            throw new IllegalArgumentException(message);
    }
}
