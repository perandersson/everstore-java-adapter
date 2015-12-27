package examples.grizzly.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import examples.grizzly.models.FinancialYearId;

import java.io.IOException;

public class FinancialIdSerializer extends JsonSerializer<FinancialYearId> {
    @Override
    public void serialize(FinancialYearId financialYearId, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(financialYearId.value.toString());
    }

    @Override
    public Class<FinancialYearId> handledType() {
        return FinancialYearId.class;
    }
}
