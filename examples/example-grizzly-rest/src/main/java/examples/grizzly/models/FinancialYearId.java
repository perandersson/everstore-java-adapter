package examples.grizzly.models;

import java.util.UUID;

public final class FinancialYearId implements Comparable<FinancialYearId> {
    public final UUID value;

    public FinancialYearId() {
        value = UUID.randomUUID();
    }

    public FinancialYearId(UUID value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FinancialYearId that = (FinancialYearId) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "FinancialYearId{" +
                "value=" + value +
                '}';
    }

    public static FinancialYearId fromString(String s) {
        return new FinancialYearId(UUID.fromString(s));
    }

    @Override
    public int compareTo(FinancialYearId o) {
        return value.compareTo(o.value);
    }
}
