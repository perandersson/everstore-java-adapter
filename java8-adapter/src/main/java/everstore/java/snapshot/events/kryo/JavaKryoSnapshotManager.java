package everstore.java.snapshot.events.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import everstore.api.JournalSize;
import everstore.api.snapshot.EventsSnapshotConfig;
import everstore.api.snapshot.EventsSnapshotEntry;
import everstore.api.snapshot.EventsSnapshotManager;
import everstore.api.validation.Validation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static everstore.api.validation.Validation.require;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.util.Optional.of;

public class JavaKryoSnapshotManager implements EventsSnapshotManager {
    public final class SnapshotEntry {
        public final Path path;
        public final long memorySize;

        public SnapshotEntry(Path path, long memorySize) {
            this.path = path;
            this.memorySize = memorySize;
        }
    }

    private final Kryo kryo = new Kryo();
    private final AtomicLong bytesUsed = new AtomicLong(0);
    private final Map<String, SnapshotEntry> entries = new HashMap<>();
    private final Path rootPath;


    public JavaKryoSnapshotManager(Path rootPath, boolean cleanOnInt) throws IOException {
        require(rootPath != null, "No root path was supplied");

        this.rootPath = rootPath;

        // Create directory if it doesn't exist
        if (!Files.exists(rootPath)) {
            Files.createDirectory(rootPath);
        }

        // Clean up the snapshot directory is we want to
        if (cleanOnInt) {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    Files.delete(file);
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return CONTINUE;
                }
            });
        }
    }


    @Override
    public void save(String name, EventsSnapshotEntry object) {

    }

    @Override
    public Optional<EventsSnapshotEntry> load(String name) throws IOException {
        synchronized (entries) {
            SnapshotEntry entry = entries.remove(name);
            if (entry == null) {

            }

        }

        SnapshotEntry entry = entries.remove(name);
        if (entry == null) {
            final Path pathToSnapshot = Paths.get(rootPath.toString(), name);
            if (Files.exists(pathToSnapshot)) {
                try (Input input = new Input(new FileInputStream(pathToSnapshot.toFile()))) {
                    final EventsSnapshotEntry snapshotEntry = kryo.readObject(input, EventsSnapshotEntry.class);
                    entries.put(name, new SnapshotEntry(pathToSnapshot, Files.size(pathToSnapshot)));
                    return of(snapshotEntry);
                }
            }
        }

        return null;
    }

    @Override
    public long memoryUsed() {
        return bytesUsed.get();
    }
}
