package everstore.java.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class CompletableFutureTest {

    @Test
    public void successOnCompletedFuture() {
        final CompletableFuture<String> futureString = CompletableFuture.completedFuture("String value");
        final Optional<String> optionalString = Optional.of(futureString);
        Assert.assertEquals("String value", optionalString.get());
    }

    @Test
    public void successOnCompletedFutureWithMultipleMappings() {
        final CompletableFuture<String> futureDoubleAsString = CompletableFuture.completedFuture("10.0");
        final Optional<String> optionalDoubleAsString = Optional.of(futureDoubleAsString);
        final Optional<Double> optionalDouble = optionalDoubleAsString.map(Double::parseDouble);
        Assert.assertEquals(new Double(10.0), optionalDouble.get());
    }

    @Test
    public void exceptionOnCompletedFutureWithMultipleMappings() {
        final CompletableFuture<String> futureDoubleAsString = CompletableFuture.completedFuture("Value not double");
        final Optional<String> optionalDoubleAsString = Optional.of(futureDoubleAsString);
        final Optional<Double> optionalDouble = optionalDoubleAsString.map(Double::parseDouble);

        try {
            optionalDouble.get();
            Assert.fail("Should not get here!");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NumberFormatException);
        }
    }

    public static class TestException1 extends RuntimeException {
    }

    @Test
    public void exceptionInFuture() {
        final Optional<String> optionalString = getExceptionThrowingException(new TestException1());
        final Optional<Long> optionalLong = optionalString.map(Long::parseLong);

        try {
            optionalLong.get();
            Assert.fail("Should not get here!");
        } catch (final Exception e) {
            Assert.assertTrue(e instanceof TestException1);
        }
    }

    @Test
    public void exceptionWhenMappingFuture() {
        final Optional<String> optionalString = getSleepingStringFuture("String value");
        final Optional<Double> optionalDouble = optionalString.map(Double::parseDouble);

        try {
            optionalDouble.get();
            Assert.fail("Should not get here!");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NumberFormatException);
        }
    }

    @Test
    public void defaultValueOnFailedFuture() {
        final Optional<String> optionalString = getExceptionThrowingException(new TestException1());
        final String rseult = optionalString.orElse("Exception occured");
        Assert.assertEquals("Exception occured", rseult);
    }

    @Test
    public void successOnRunnableFuture() throws InterruptedException {
        final Optional<String> optionalString = getSleepingStringFuture("String value");
        final Optional<Character> optionalChar = optionalString.map(val -> val.charAt(0));

        final AtomicReference<Character> result = new AtomicReference<>('0');
        final CountDownLatch latch = new CountDownLatch(1);
        optionalChar.each(c -> {
            result.set(c);
            latch.countDown();
        });

        latch.await();

        Assert.assertEquals(new Character('S'), result.get());
    }

    @Test
    public void successfulFlatMappedFutures() {
        final Optional<String> optionalString = getSleepingStringFuture("10");
        final Optional<String> optionalString2 = optionalString.flatMap(result -> getSleepingStringFuture(result + "2"));
        final Optional<Integer> optionalInt = optionalString2.map(Integer::parseInt);

        Assert.assertEquals(new Integer(102), optionalInt.get());
    }

    @Test
    public void exceptionOnFlatMappedFuture() {
        final Optional<String> optionalString = getSleepingStringFuture("10");
        final Optional<String> optionalString2 = optionalString.flatMap(result -> getExceptionThrowingException(new TestException1()));
        final Optional<Integer> optionalInt = optionalString2.map(Integer::parseInt);

        try {
            optionalInt.get();
            Assert.fail("Should not get here!");
        } catch (final Exception e) {
            Assert.assertTrue(e instanceof TestException1);
        }
    }


    private Optional<String> getSleepingStringFuture(final String value) {
        return Optional.of(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return value;
        }));
    }

    private <T extends RuntimeException> Optional<String> getExceptionThrowingException(T exception) {
        return Optional.of(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            throw exception;
        }));
    }
}
