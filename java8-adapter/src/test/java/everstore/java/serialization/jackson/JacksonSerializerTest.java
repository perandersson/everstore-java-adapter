package everstore.java.serialization.jackson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JacksonSerializerTest {

    private JacksonSerializer unitToTest = new JacksonSerializer();

    @Test
    public void serializerShouldBeAbleToSerializeASimpleClass() {
        final SimpleEvent event = new SimpleEvent("test");
        final String serializedEvent = unitToTest.convertToString(event);

        assertEquals("everstore.java.serialization.jackson.SimpleEvent {\"value\":\"test\"}",
                serializedEvent);
    }

    @Test
    public void serializerShouldBeAbleToSerializeASimpleGetterSetterClass() {
        final SimpleEventWithGettersAndSetters event = new SimpleEventWithGettersAndSetters();
        event.setValue("test");
        final String serializedEvent = unitToTest.convertToString(event);

        assertEquals("everstore.java.serialization.jackson.SimpleEventWithGettersAndSetters {\"value\":\"test\"}",
                serializedEvent);
    }

    @Test
    public void serializerShouldBeAbleToDeserializeASimpleClass() {
        final String serializedEvent =
                "everstore.java.serialization.jackson.SimpleEvent {\"value\":\"test\"}";
        final SimpleEvent event = unitToTest.convertFromString(serializedEvent);

        assertNotNull(event);
        assertEquals(new SimpleEvent("test"), event);
    }

    @Test
    public void serializerShouldBeAbleToDeserializeAGetterSetterClass() {
        final String serializedEvent =
                "everstore.java.serialization.jackson.SimpleEventWithGettersAndSetters {\"value\":\"test\"}";
        final SimpleEventWithGettersAndSetters event = unitToTest.convertFromString(serializedEvent);

        assertNotNull(event);

        final SimpleEventWithGettersAndSetters expected = new SimpleEventWithGettersAndSetters();
        expected.setValue("test");
        assertEquals(expected, event);
    }

    @Test
    public void serializerShouldBeAbleToSerializeACompleteClass() {
        final ComplexEvent event = new ComplexEvent(new SimpleEvent("test"), 12345);
        final String serializedEvent = unitToTest.convertToString(event);

        assertEquals("everstore.java.serialization.jackson.ComplexEvent {\"inner\":{\"value\":\"test\"},\"value\":12345}",
                serializedEvent);
    }

    @Test
    public void serializerShouldBeAbleToDeserializeAComplexClass() {
        final String serializedEvent =
                "everstore.java.serialization.jackson.ComplexEvent {\"inner\":{\"value\":\"test\"},\"value\":12345}";
        final ComplexEvent event = unitToTest.convertFromString(serializedEvent);

        assertNotNull(event);
        assertEquals(new ComplexEvent(new SimpleEvent("test"), 12345), event);
    }

    @Test
    public void serializerShouldBeAbleToDeserializeAnIncompleteClass() {
        final String serializedEvent =
                "everstore.java.serialization.jackson.ComplexEvent {\"inner\":{\"value\":\"test\"}}";
        final ComplexEvent event = unitToTest.convertFromString(serializedEvent);

        assertNotNull(event);
        assertEquals(new ComplexEvent(new SimpleEvent("test"), 54321), event);
    }
}
