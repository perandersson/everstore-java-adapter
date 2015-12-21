package everstore.java.utils;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ExceptionOptional<T, E extends Throwable> implements Optional<T> {
    private final E exception;

    public ExceptionOptional(final E exception) {
        this.exception = exception;
    }

    @Override
    public Optional<T> filter(ThrowablePredicate<? super T> predicate) {
        return this;
    }

    @Override
    public <U> Optional<U> map(ThrowableFunction<? super T, ? extends U> mapper) {
        return Optional.<U, E>exceptionThrown(this.exception);
    }

    @Override
    public <U> Optional<U> flatMap(ThrowableFunction<? super T, Optional<U>> mapper) {
        return Optional.<U, E>exceptionThrown(this.exception);
    }

    @Override
    public Optional<T> each(ThrowableConsumer<? super T> consumer) {
        return this;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public T get() {
        throw new NoSuchElementException("No value present");
    }

    @Override
    public T orNull() {
        return null;
    }

    @Override
    public T orElse(T other) {
        return other;
    }

    @Override
    public T orElseGet(Supplier<? extends T> other) {
        return other.get();
    }

    @Override
    public <E2 extends Throwable> T orElseThrow(E2 e) throws E2 {
        throw e;
    }

    @Override
    public <E2 extends Throwable> T orElseThrow(Supplier<? extends E2> exceptionSupplier) throws E2 {
        throw exceptionSupplier.get();
    }

    @Override
    public Optional<T> exceptionally(Consumer<Throwable> exceptionOccurred) {
        exceptionOccurred.accept(exception);
        return this;
    }

    @Override
    public String toString() {
        return "Optional.exceptionOptional(exception={" + exception + "})";
    }
}
