package everstore.java.serialization.jackson;

public class SimpleEvent {
    public final String value;

    protected SimpleEvent() {
        value = "";
    }

    public SimpleEvent(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleEvent that = (SimpleEvent) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
