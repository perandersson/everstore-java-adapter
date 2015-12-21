package everstore.java.utils;

import org.junit.Test;

import java.util.function.Supplier;

import static everstore.java.utils.Optional.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BasicObjectTest {

    @Test
    public void emptyTest() {
        final Optional value = empty();

        assertEquals(empty(), value);
        assertEquals("Optional.empty", value.toString());
        assertTrue(value.isEmpty());
        assertFalse(value.isPresent());
    }

    public static class Object1 {

    }

    @Test
    public void existingTest() {
        final Object1 raw = new Object1();
        final Optional<Object1> value = Optional.of(raw);

        assertFalse(value.isEmpty());
        assertTrue(value.isPresent());
        assertEquals(raw.toString(), value.toString());
        assertEquals(value, Optional.of(raw));
    }

    public static class TestException1 extends Exception {

    }

    @Test(expected = TestException1.class)
    public void exceptionThrownWhenEmpty1() throws TestException1 {
        empty().orElseThrow(new TestException1());
    }

    @Test(expected = TestException1.class)
    public void exceptionThrownWhenEmpty2() throws TestException1 {
        empty().orElseThrow((Supplier<? extends TestException1>) TestException1::new);
    }

    @Test
    public void mapOptional() {
        Optional<String> value = Optional.of("1.0");
        Optional<Double> doubleValue = value.map(Double::parseDouble);

        assertEquals(new Double(1.0), doubleValue.get());
        assertEquals(Optional.of(1.0), doubleValue);
    }

    @Test
    public void mapEmpty() {
        Optional<Double> doubleValue = Optional.<String>empty().map(Double::parseDouble);
        assertEquals(doubleValue, empty());
    }

}
