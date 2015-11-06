package examples.console;

import everstore.api.Adapter;
import everstore.api.AdapterConfig;
import everstore.api.Transaction;
import everstore.java.serialization.jackson.JacksonSerializer;
import everstore.vanilla.VanillaDataStorageFactory;

import java.io.IOException;
import java.util.concurrent.Future;

public class Example1 {
    public static void main(String[] args) throws IOException {
        // Adapter configuration
        final AdapterConfig config = new AdapterConfig("admin", "passwd",
                "localhost", (short) 6929, 6, new JacksonSerializer(),
                new VanillaDataStorageFactory());

        // Connect to the server
        final Adapter adapter = new Adapter(config);
        adapter.connect();

        // Open transaction to a journal
        Future<Transaction> transaction1 = adapter.openTransaction("/java/user/per.andersson@funnic.com");


        // Close the connection
        adapter.close();
    }
}
