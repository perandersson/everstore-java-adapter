package examples.console.example2.models;

public class UserId implements Comparable<UserId> {
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

    @Override
    public int compareTo(final UserId rhs) {
        if (value > rhs.value)
            return -1;
        if (value < rhs.value)
            return 1;
        return 0;
    }
}
