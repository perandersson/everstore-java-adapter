package examples.console.example2;

public class UserId {
    public final long value;

    public UserId(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UserId{" +
                "value=" + value +
                '}';
    }
}
