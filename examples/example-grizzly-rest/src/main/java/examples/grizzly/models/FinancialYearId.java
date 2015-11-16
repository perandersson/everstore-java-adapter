package examples.grizzly.models;

public final class FinancialYearId implements Comparable<FinancialYearId> {
    public final long value;

    protected FinancialYearId() {
        value = 0;
    }

    public FinancialYearId(long value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FinancialYearId that = (FinancialYearId) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {
        return "FinancialYearId{" +
                "value=" + value +
                '}';
    }

    @Override
    public int compareTo(final FinancialYearId rhs) {
        if (value > rhs.value)
            return -1;
        if (value < rhs.value)
            return 1;
        return 0;
    }

    public static final FinancialYearId ZERO = new FinancialYearId(0);
}
