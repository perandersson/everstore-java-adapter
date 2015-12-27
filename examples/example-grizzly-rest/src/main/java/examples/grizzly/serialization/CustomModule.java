package examples.grizzly.serialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import examples.grizzly.models.FinancialYearId;
import examples.grizzly.models.OrgId;

public class CustomModule extends SimpleModule {
    public CustomModule() {
        super("CustomModule", new Version(1, 0, 0, null));

        addSerializer(new OrgIdSerializer());
        addDeserializer(OrgId.class, new OrgIdDeserializer());

        addSerializer(new FinancialIdSerializer());
        addDeserializer(FinancialYearId.class, new FinancialYearIdDeserializer());

    }
}
