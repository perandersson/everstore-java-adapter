package examples.grizzly.providers;

import examples.grizzly.models.FinancialYearId;
import org.glassfish.jersey.message.internal.AbstractMessageReaderWriterProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

@Provider
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class FinIdProvider extends AbstractMessageReaderWriterProvider<FinancialYearId> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return FinancialYearId.class.isAssignableFrom(type);
    }

    @Override
    public FinancialYearId readFrom(Class<FinancialYearId> type, Type genericType,
                                    Annotation[] annotations, MediaType mediaType,
                                    MultivaluedMap<String, String> httpHeaders,
                                    InputStream entityStream) throws IOException, WebApplicationException {
        final String value = readFromAsString(entityStream, APPLICATION_JSON_TYPE);
        if (value.length() > 0) {
            return FinancialYearId.fromString(value);
        }
        return null;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return FinancialYearId.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(FinancialYearId financialYearId, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        String value = financialYearId.value.toString();
        writeToAsString(value, entityStream, APPLICATION_JSON_TYPE);
    }
}
