package everstore.maven;

public interface EventSerializable {
    void serializeEvent(EventWriter writer);
}
