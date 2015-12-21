package examples.grizzly.models;

import java.util.UUID;

public final class OrgId implements Comparable<OrgId> {
    public final UUID value;

    public OrgId() {
        value = UUID.randomUUID();
    }

    public OrgId(final UUID value) {
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
    public int compareTo(OrgId o) {
        return value.compareTo(o.value);
    }

    public static OrgId fromString(final String s) {
        return new OrgId(UUID.fromString(s));
    }
}
