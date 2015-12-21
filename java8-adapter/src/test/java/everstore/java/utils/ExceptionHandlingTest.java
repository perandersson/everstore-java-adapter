package everstore.java.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExceptionHandlingTest {

    @Test
    public void testException1() {
        final Optional<String> stringVal = Optional.of("Hello World");
        final Integer intVal = stringVal.map(s -> s.charAt(1000))
                .map(c -> Character.digit(c, 10))
                .orElse(10);

        assertEquals(new Integer(10), intVal);
    }

    @Test
    public void testException2() {
        final Optional<String> stringOptional = Optional.ofNullable("String value");
        final Optional<String> stringOptionalAfterMaps = stringOptional
                .map(Long::parseLong)
                .map(l -> Long.toString(l))
                .filter(s -> !s.isEmpty());
        final String stringVal = stringOptionalAfterMaps.orElse("Missing");

        assertEquals("Optional.exceptionOptional(exception={java.lang.NumberFormatException: For input string: \"String value\"})",
                stringOptionalAfterMaps.toString());
        assertEquals("Missing", stringVal);
    }

//    @Test
//    public void testException3() {
//        final Optional<String> stringOptional = Optional.ofNullable("String value");
//        final Optional<Long> longOptional = stringOptional
//                .map(Long::parseLong);
//        longOptional.catchException()
//    }
}
