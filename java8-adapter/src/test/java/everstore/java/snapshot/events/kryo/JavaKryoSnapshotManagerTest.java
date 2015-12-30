package everstore.java.snapshot.events.kryo;

import everstore.api.JournalSize;
import everstore.api.snapshot.EventsSnapshotEntry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

public class JavaKryoSnapshotManagerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void validSnapshotEntrySaved() throws IOException {
        final Path rootPath = folder.newFolder().toPath();
        final JavaKryoSnapshotManager unitToTest = new JavaKryoSnapshotManager(rootPath, false, 100);

        final JournalSize journalSize = new JournalSize(100);
        final JournalSize offset = new JournalSize(0);
        final TestEvent event = new TestEvent(100);

        EventsSnapshotEntry objectToSave = new EventsSnapshotEntry(journalSize,
                Collections.singletonList(event));
        unitToTest.save("/journal/item1", objectToSave);

        final long totalSize = unitToTest.memoryUsed();
        final Optional<EventsSnapshotEntry> loadedObject = unitToTest.load("/journal/item1", offset);

        assertEquals(92, totalSize);
        assertNotNull(loadedObject);
        assertTrue(loadedObject.isPresent());
        assertEquals(journalSize, loadedObject.get().journalSize);
        assertEquals(1, loadedObject.get().events.size());
        assertTrue(loadedObject.get().events.get(0) instanceof TestEvent);
        assertEquals(event.value, ((TestEvent) loadedObject.get().events.get(0)).value);

    }

    @Test
    public void deleteFilesIfMemoryIsToLarge() throws IOException {
        final Path rootPath = folder.newFolder().toPath();
        final JavaKryoSnapshotManager unitToTest = new JavaKryoSnapshotManager(rootPath, false, 100);

        final JournalSize journalSize = new JournalSize(100);
        final TestEvent event = new TestEvent(100);

        EventsSnapshotEntry objectToSave = new EventsSnapshotEntry(journalSize,
                Collections.singletonList(event));
        unitToTest.save("/journal/item1", objectToSave);

        final long firstTotalSize = unitToTest.memoryUsed();

        unitToTest.save("/journal/item2", objectToSave);

        final long secondTotalSize = unitToTest.memoryUsed();

        assertEquals(92, firstTotalSize);
        assertEquals(92, secondTotalSize);
    }
}
