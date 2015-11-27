package everstore.maven;

public interface EventWriter {
    void put(String key, int value);
    void put(String key, String value);
    void put(String key, EventSerializable s);
}
