package examples.grizzly;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import everstore.api.Adapter;
import everstore.api.AdapterConfig;
import everstore.api.snapshot.EventsSnapshotConfig;
import everstore.java.serialization.jackson.LocalDateModule;
import everstore.vanilla.VanillaDataStorageFactory;
import examples.grizzly.serialization.CustomModule;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import static everstore.java.serialization.Serializers.defaultSerializer;
import static everstore.java.snapshot.events.SnapshotManagers.defaultFactory;
import static java.util.Optional.of;

public class App {
    private static final URI BASE_URI = URI.create("http://localhost:8080/");

    /**
     * Entry-point for this REST example
     */
    public static void main(String[] args) throws IOException {
        final Adapter adapter = createAdapter();

        try {
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, create(adapter));
            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
            server.start();
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            adapter.close();
        }
    }

    /**
     * Create an everstore adapter
     *
     * @return
     * @throws IOException
     */
    private static Adapter createAdapter() throws IOException {
        // Configure the adapter
        final Path rootPath = Paths.get("snapshotdir");
        final EventsSnapshotConfig snapshotConfig = new EventsSnapshotConfig(rootPath, defaultFactory());
        final AdapterConfig adapterConfig = new AdapterConfig("admin", "passwd",
                "localhost", (short) 6929, 6, 2000, 65526,
                defaultSerializer(), new VanillaDataStorageFactory(),
                of(snapshotConfig));

        // Connect to the server
        Adapter adapter = new Adapter(adapterConfig);
        adapter.connect();
        return adapter;
    }

    /**
     * Configure the Grizzly Http Server
     *
     * @param adapter
     * @return
     * @throws IOException
     */
    private static ResourceConfig create(final Adapter adapter) throws IOException {
        ResourceConfig config = new ResourceConfig();
        config.register(new AppModule(adapter));
        config.packages("examples.grizzly");

        final ObjectMapper mapper = new ObjectMapper();
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        jacksonProvider.setMapper(mapper);

        mapper.registerModule(new LocalDateModule());
        mapper.registerModule(new CustomModule());
        config.register(jacksonProvider);

        return config;
    }
}
