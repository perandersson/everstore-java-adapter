package examples.grizzly.events;

public final class OrganizationCreated implements OrganizationEvent {
    public final String name;

    protected OrganizationCreated() {
        name = "";
    }

    public OrganizationCreated(String name) {
        this.name = name;
    }
}
