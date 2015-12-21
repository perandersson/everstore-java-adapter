package everstore.java.utils;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Empty<T> implements Optional<T> {

    @Override
    public Optional<T> filter(ThrowablePredicate<? super T> predicate) {
        return this;
    }

    @Override
    public <U> Optional<U> map(ThrowableFunction<? super T, ? extends U> mapper) {
        return Optional.<U>empty();
    }

    @Override
    public <U> Optional<U> flatMap(ThrowableFunction<? super T, Optional<U>> mapper) {
        return Optional.<U>empty();
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
        return true;
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
    public <E extends Throwable> T orElseThrow(E e) throws E {
        throw e;
    }

    @Override
    public <E extends Throwable> T orElseThrow(Supplier<? extends E> exceptionSupplier) throws E {
        throw exceptionSupplier.get();
    }

    @Override
    public Optional<T> exceptionally(Consumer<Throwable> exceptionOccurred) {
        return this;
    }

    @Override
    public String toString() {
        return "Optional.empty";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Empty;
    }
}
