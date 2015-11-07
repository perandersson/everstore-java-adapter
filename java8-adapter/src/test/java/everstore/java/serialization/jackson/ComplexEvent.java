package everstore.java.serialization.jackson;

public class ComplexEvent {
    public final SimpleEvent inner;
    public final int value;

    protected ComplexEvent() {
        inner = new SimpleEvent("");
        value = 54321; // Default value
    }

    public ComplexEvent(SimpleEvent inner, int value) {
        this.inner = inner;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComplexEvent that = (ComplexEvent) o;

        if (value != that.value) return false;
        return inner.equals(that.inner);

    }

    @Override
    public int hashCode() {
        int result = inner.hashCode();
        result = 31 * result + value;
        return result;
    }
}
