package everstore.java.serialization;

import everstore.api.serialization.Serializer;
import everstore.java.serialization.jackson.JacksonSerializer;

public final class Serializers {
    public static final Serializer defaultSerializer() {
        return new JacksonSerializer();
    }
}
