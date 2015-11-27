package examples.grizzly.models;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static everstore.api.validation.Validation.require;
import static java.util.UUID.randomUUID;

public final class OrgId implements Comparable<OrgId> {
    public final UUID value;

    public OrgId() {
        value = randomUUID();
    }

    public OrgId(@NotNull UUID value) {
        require(value != null, "Organization ID must be set");
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgId orgId = (OrgId) o;

        return value.equals(orgId.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "OrgId{" +
                "value=" + value +
                '}';
    }

    @Override
    public int compareTo(final OrgId rhs) {
        return value.compareTo(rhs.value);
    }

    public static OrgId fromString(String s) {
        return new OrgId(UUID.fromString(s));
    }
}
