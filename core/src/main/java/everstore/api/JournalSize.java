package everstore.api;

public final class JournalSize {
    public final int value;

    public JournalSize(int value) {
        this.value = value;
    }

    public boolean isZero() {
        return value == 0;
    }

    public boolean isLargerThan(JournalSize rhs) {
        return value > rhs.value;
    }

    public boolean isSmallerThen(JournalSize rhs) {
        return value < rhs.value;
    }

    public int sub(JournalSize rhs) { return value - rhs.value; }

    public static final JournalSize ZERO = new JournalSize(0);
}
