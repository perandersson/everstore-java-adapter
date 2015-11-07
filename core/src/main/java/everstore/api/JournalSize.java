package everstore.api;

public final class JournalSize {
    public final int value;

    protected JournalSize() {
        this.value = 0;
    }

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

    public int sub(JournalSize rhs) {
        return value - rhs.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JournalSize that = (JournalSize) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return value;
    }

    public static final JournalSize ZERO = new JournalSize();
}
