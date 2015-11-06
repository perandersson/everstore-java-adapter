package everstore.java.serialization.jackson;

import everstore.api.serialization.Serializer;

import java.util.Set;

public class JacksonSerializer implements Serializer {

    @Override
    public <T> String convertToString(T object) {
        return null;
    }

    @Override
    public Object convertFromString(String value) {
        return null;
    }

    @Override
    public <T> Set<String> convertToTypes(T object) {
        return null;
    }
}
