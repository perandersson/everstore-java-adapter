package everstore.api.serialization;

import java.util.Set;

public interface Serializer {

    <T> String convertToString(T object);

    Object convertFromString(String value);

    <T> Set<String> convertToTypes(T object);
}
