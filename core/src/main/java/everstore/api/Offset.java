package everstore.api;

public final class Offset {
    public final int value;

    protected Offset() {
        this.value = 0;
    }

    public Offset(int value) {
        this.value = value;
    }

    public boolean isLargerThan(Offset offset) {
        return value > offset.value;
    }

    public boolean isSmallerThen(Offset offset) {
        return value < offset.value;
    }

    public int sub(Offset offset) {
        return value - offset.value;
    }

    public static final Offset ZERO = new Offset();
}
