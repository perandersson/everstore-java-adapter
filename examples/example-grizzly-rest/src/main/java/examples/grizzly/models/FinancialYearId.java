package examples.grizzly.models;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static everstore.api.validation.Validation.require;
import static java.util.UUID.randomUUID;

public final class FinancialYearId implements Comparable<FinancialYearId> {
    public final UUID value;

    public FinancialYearId() {
        value = randomUUID();
    }

    public FinancialYearId(@NotNull UUID value) {
        require(value != null, "You must supply a valid financial year ID");
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

    @Override
    public int compareTo(final FinancialYearId rhs) {
        return value.compareTo(rhs.value);
    }

    public static FinancialYearId fromString(String id) {
        return new FinancialYearId(UUID.fromString(id));
    }

    public static final FinancialYearId ZERO = new FinancialYearId();
}
