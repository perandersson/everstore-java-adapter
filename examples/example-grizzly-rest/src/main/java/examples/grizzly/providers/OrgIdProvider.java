package examples.grizzly.providers;

import examples.grizzly.models.OrgId;
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
public class OrgIdProvider extends AbstractMessageReaderWriterProvider<OrgId> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return OrgId.class.isAssignableFrom(type);
    }

    @Override
    public OrgId readFrom(Class<OrgId> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws IOException, WebApplicationException {
        final String value = readFromAsString(entityStream, APPLICATION_JSON_TYPE);
        if (value.length() > 0) {
            Long id = Long.parseLong(value);
            return new OrgId(id);
        }
        return null;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return OrgId.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(OrgId orgId, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        String value = Long.toString(orgId.value);
        writeToAsString(value, entityStream, APPLICATION_JSON_TYPE);
    }
}
