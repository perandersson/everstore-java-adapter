package examples.grizzly.models;

public final class OrgId implements Comparable<OrgId> {
    public final long value;

    protected OrgId() {
        value = 0;
    }

    public OrgId(long value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgId orgId = (OrgId) o;

        return value == orgId.value;

    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {
        return "OrgId{" +
                "value=" + value +
                '}';
    }

    @Override
    public int compareTo(final OrgId rhs) {
        if (value > rhs.value)
            return -1;
        if (value < rhs.value)
            return 1;
        return 0;
    }
}
