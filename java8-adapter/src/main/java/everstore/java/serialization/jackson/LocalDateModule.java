package everstore.java.serialization.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.LocalDate;

public class LocalDateModule extends SimpleModule {
    public LocalDateModule() {
        super("LocalDateModule", new Version(1, 0, 0, null));
        addSerializer(new LocalDateSerializer());
        addDeserializer(LocalDate.class, new LocalDateDeserializer());
    }
}
