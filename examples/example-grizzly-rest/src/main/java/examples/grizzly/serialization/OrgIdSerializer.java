package examples.grizzly.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import examples.grizzly.models.OrgId;

import java.io.IOException;

public class OrgIdSerializer extends JsonSerializer<OrgId> {
    @Override
    public void serialize(OrgId orgId, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(orgId.value.toString());
    }

    @Override
    public Class<OrgId> handledType() {
        return OrgId.class;
    }
}
