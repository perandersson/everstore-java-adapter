package everstore.java.snapshot.events.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import everstore.api.JournalSize;
import everstore.api.snapshot.EventsSnapshotEntry;
import everstore.api.snapshot.EventsSnapshotManager;
import everstore.api.snapshot.EverstoreIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import static everstore.api.validation.Validation.require;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.*;
import static java.util.Optional.of;

/**
 * SnapshotManager that uses Kryo as framework for serialize and deserialize the supplied events.
 */
public class JavaKryoSnapshotManager implements EventsSnapshotManager {
    private final Logger log = LoggerFactory.getLogger(JavaKryoSnapshotManager.class);

    public final class SnapshotEntry {
        public final long memorySize;
        public final JournalSize journalSize;

        public SnapshotEntry(long memorySize, JournalSize journalSize) {
            this.memorySize = memorySize;
            this.journalSize = journalSize;
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
    private final ConcurrentSkipListMap<String, SnapshotEntry> entries = new ConcurrentSkipListMap<>();
    private final Path rootPath;
    private final long maxBytesAllowed;

    public JavaKryoSnapshotManager(final Path rootPath, final boolean cleanOnInt,
                                   final long maxBytesAllowed) throws IOException {
        require(rootPath != null, "No root path was supplied");

        this.rootPath = rootPath;
        this.maxBytesAllowed = maxBytesAllowed;

        // Create directory if it doesn't exist
        if (!Files.exists(rootPath)) {
            createDirectory(rootPath);
        }
        require(isDirectory(rootPath), "The root path must be a directory");

        // Clean up the snapshot directory if requested
        if (cleanOnInt) {
            walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    Files.delete(file);
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (!dir.equals(rootPath)) {
                        Files.delete(dir);
                    }
                    return CONTINUE;
                }
            });
        } else {
            log.debug("Starting to prepare snapshots");
            walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    final EventsSnapshotEntry entry = deserialize(file);
                    entries.put(file.toString(), new SnapshotEntry(Files.size(file), entry.journalSize));
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
            final SnapshotEntry snapshotEntry = getEntryOrDefault(fullPath, new SnapshotEntry(0, JournalSize.ZERO));
            if (object.journalSize.isSmallerOrEqualsThen(snapshotEntry.journalSize)) {
                return;
            }

            createIfMissing(fullPath);

            try (FileOutputStream outputStream = new FileOutputStream(fullPath.toFile())) {
                final FileChannel channel = outputStream.getChannel();

                // Ensure that only one is writing to the same file at the same time
                try (FileLock ignored = channel.lock()) {
                    // Start writing at the beginning
                    channel.position(0);

                    // Serialize and save data
                    final Output output = new Output(outputStream);
                    kryo().writeObject(output, object);
                    output.flush();
                    final long fileSize = channel.position();
                    channel.truncate(fileSize);
                    replaceEntry(fullPath, fileSize, object.journalSize);
                    bytesUsed.addAndGet(fileSize);
                }
            }

            freeSpace();
        } catch (IOException e) {
            throw new EverstoreIOException(e);
        }
    }

    private void createIfMissing(Path fullPath) {
        try {
            createDirectories(fullPath.getParent());
            //createFile(fullPath);
        } catch (IOException e) {
            log.error("Could not create the necessary directories for path: " + fullPath, e);
            // TODO: Decide what to do if it's not possible to create the full path to the snapshot file.
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
    public Optional<EventsSnapshotEntry> load(String name, JournalSize offset) throws EverstoreIOException {
        require(name.length() > 0, "You must supply a valid name of the snapshot entry");

        final Path fullPath = getFullPath(name);
        final SnapshotEntry snapshotEntry = getEntryOrDefault(fullPath, new SnapshotEntry(0, JournalSize.ZERO));
        if (snapshotEntry.journalSize.isLargerThan(offset)) {
            try {
                if (Files.exists(fullPath)) {
                    return of(deserialize(fullPath));
                }
            } catch (IOException e) {
                throw new EverstoreIOException(e);
            }
        }

        return Optional.empty();
    }

    private EventsSnapshotEntry deserialize(Path fullPath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(fullPath.toFile())) {
            final FileChannel channel = inputStream.getChannel();

            // Ensure that only one is reading and writing to the same file at the same time
            try (FileLock ignored = channel.lock(0, Long.MAX_VALUE, true)) {
                final Input input = new Input(inputStream);
                return kryo().readObject(input, EventsSnapshotEntry.class);
            }
        }
    }

    private SnapshotEntry getEntryOrDefault(Path path, SnapshotEntry defaultObj) {
        synchronized (entries) {
            final SnapshotEntry entry = entries.get(path.toString());
            if (entry == null)
                return defaultObj;
            else
                return entry;
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
