package everstore.java.serialization.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import everstore.api.exception.EverstoreSerializerFailed;
import everstore.api.serialization.Serializer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static everstore.api.validation.Validation.require;

public class JacksonSerializer implements Serializer {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ClassLoader classLoader = JacksonSerializer.class.getClassLoader();

    public JacksonSerializer() {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new LocalDateModule());
    }

    @Override
    public <T> String convertToString(T object) {
        try {
            return object.getClass().getName() + " " + mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new EverstoreSerializerFailed(e);
        }
    }

    @Override
    public <T> T convertFromString(String value) {
        final int idx = value.indexOf(' ');
        require(idx != -1, "Incoming data do not fit the required format [className] [data]");

        final String className = value.substring(0, idx);
        final String json = value.substring(idx + 1);

        final Class<T> clazz;
        try {
            clazz = (Class<T>) classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new EverstoreSerializerFailed("Could not load class: " + className, e);
        }

        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new EverstoreSerializerFailed("Could not deserialize to object", e);
        }
    }

    @Override
    public <T> Set<String> convertToTypes(T object) {
        final HashSet<String> types = new HashSet<>();
        Class<?>[] interfaces = object.getClass().getInterfaces();
        for (Class<?> anInterface : interfaces) {
            types.add(anInterface.getSimpleName());
        }
        return types;
    }
}
