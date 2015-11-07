package everstore.java.snapshot.events.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import everstore.api.JournalSize;
import everstore.api.snapshot.EventsSnapshotEntry;
import everstore.api.snapshot.EventsSnapshotManager;
import everstore.api.snapshot.EverstoreIOException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static everstore.api.validation.Validation.require;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.*;
import static java.util.Optional.of;

/**
 * SnapshotManager that uses Kryo as framework for serialize and deserialize the supplied events.
 */
public class JavaKryoSnapshotManager implements EventsSnapshotManager {
    public final class SnapshotEntry {
        public final long memorySize;
        public final JournalSize journalSize;

        public SnapshotEntry(long memorySize, JournalSize journalSize) {
            this.memorySize = memorySize;
            this.journalSize = journalSize;
        }

        /**
         * @return TRUE if this entry needs to be reloaded.
         */
        public boolean isDirty() {
            return journalSize.isZero();
        }
    }

    /**
     * A pool of kryo instances - per thread.
     *
     * @see https://github.com/EsotericSoftware/kryo#pooling-kryo-instances
     */
    private ThreadLocal<SoftReference<Kryo>> kryoPool = new ThreadLocal<SoftReference<Kryo>>() {
        @Override
        protected SoftReference<Kryo> initialValue() {
            Kryo instance = new Kryo();
            return new SoftReference<>(instance);
        }
    };

    private final AtomicLong bytesUsed = new AtomicLong(0);
    private final Map<String, SnapshotEntry> entries = new HashMap<>();
    private final Path rootPath;
    private final long maxBytesAllowed;

    public JavaKryoSnapshotManager(Path rootPath, boolean cleanOnInt, long maxBytesAllowed) throws IOException {
        require(rootPath != null, "No root path was supplied");

        this.rootPath = rootPath;
        this.maxBytesAllowed = maxBytesAllowed;

        // Create directory if it doesn't exist
        if (!Files.exists(rootPath)) {
            createDirectory(rootPath);
        }

        require(isDirectory(rootPath), "The root path must be a directory");

        // Clean up the snapshot directory is we want to
        if (cleanOnInt) {
            walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
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
        } else {
            walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    entries.put(file.toString(), new SnapshotEntry(Files.size(file), JournalSize.ZERO));
                    return CONTINUE;
                }
            });
        }
    }


    @Override
    public void save(String name, EventsSnapshotEntry object) throws EverstoreIOException {
        require(name.length() > 0, "You must supply a valid name of the snapshot entry");
        require(object != null, "You must supply a valid object to be serialized");

        try {
            final Path fullPath = getFullPath(name);
            createIfMissing(fullPath);

            final ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
            try (Output output = new Output(stream)) {
                kryo().writeObject(output, object);
                output.flush();
                final byte[] array = stream.toByteArray();
                final long fileSize = array.length;
                Files.write(fullPath, array, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                replaceEntry(fullPath, fileSize, object.journalSize);
                bytesUsed.addAndGet(fileSize);
            }

            freeSpace();
        } catch (IOException e) {
            throw new EverstoreIOException(e);
        }
    }

    private void createIfMissing(Path fullPath) {
        try {
            createDirectories(fullPath.getParent());
            createFile(fullPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Free up space on the hdd if necessary.
     *
     * @throws IOException
     */
    private void freeSpace() throws IOException {
        while (bytesUsed.get() > maxBytesAllowed) {
            final Map.Entry<String, SnapshotEntry> firstEntry;
            synchronized (entries) {
                firstEntry = entries.entrySet().iterator().next();
                entries.remove(firstEntry.getKey());
            }

            final Path entryPath = Paths.get(firstEntry.getKey());
            final SnapshotEntry entry = firstEntry.getValue();

            bytesUsed.addAndGet(-entry.memorySize);
            deleteIfExists(entryPath);
        }
    }

    @Override
    public Optional<EventsSnapshotEntry> load(String name) throws EverstoreIOException {
        require(name.length() > 0, "You must supply a valid name of the snapshot entry");

        final Path fullPath = getFullPath(name);
        final SnapshotEntry entry = getEntry(fullPath);

        try {
            if (entry != null) {
                if (Files.exists(fullPath)) {
                    try (Input input = new Input(new FileInputStream(fullPath.toFile()))) {
                        final EventsSnapshotEntry snapshotEntry = kryo().readObject(input, EventsSnapshotEntry.class);
                        if (entry.isDirty())
                            replaceEntry(fullPath, snapshotEntry, Files.size(fullPath));
                        return of(snapshotEntry);
                    }
                }
            }
        } catch (IOException e) {
            throw new EverstoreIOException(e);
        }

        return Optional.empty();
    }

    private SnapshotEntry getEntry(Path path) {
        synchronized (entries) {
            return entries.get(path.toString());
        }
    }

    private void replaceEntry(Path path, EventsSnapshotEntry entry, long size) {
        synchronized (entries) {
            replaceEntry(path, size, entry.journalSize);
        }
    }

    private void replaceEntry(Path path, long size, JournalSize journalSize) {
        synchronized (entries) {
            entries.put(path.toString(), new SnapshotEntry(size, journalSize));
        }
    }

    private Path getFullPath(final String name) {
        return Paths.get(rootPath.toString(), name);
    }

    private Kryo kryo() {
        final SoftReference<Kryo> ref = kryoPool.get();
        Kryo instance = ref.get();
        if (ref.get() == null) {
            instance = new Kryo();
            kryoPool.set(new SoftReference<>(instance));
        }
        return instance;
    }

    @Override
    public long memoryUsed() {
        return bytesUsed.get();
    }
}
