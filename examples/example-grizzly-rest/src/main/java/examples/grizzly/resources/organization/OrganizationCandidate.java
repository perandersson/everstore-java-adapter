package examples.grizzly.resources.organization;

import static everstore.api.validation.Validation.require;

public final class OrganizationCandidate {
    public final String name;

    protected OrganizationCandidate() {
        name = null;
    }

    public OrganizationCandidate(String name) {
        require(name != null && !name.isEmpty(), "A valid name is required");

        this.name = name;
    }
}
