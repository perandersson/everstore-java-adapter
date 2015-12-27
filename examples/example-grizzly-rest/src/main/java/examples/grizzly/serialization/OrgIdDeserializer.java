package examples.grizzly.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import examples.grizzly.models.OrgId;

import java.io.IOException;

public class OrgIdDeserializer extends JsonDeserializer<OrgId> {
    @Override
    public OrgId deserialize(JsonParser jsonParser,
                             DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        final ObjectCodec oc = jsonParser.getCodec();
        final TextNode node = oc.readTree(jsonParser);
        final String idString = node.textValue();
        return OrgId.fromString(idString);
    }
}
