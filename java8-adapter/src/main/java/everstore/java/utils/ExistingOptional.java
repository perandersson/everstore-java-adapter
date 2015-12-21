package everstore.java.utils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ExistingOptional<T> implements Optional<T> {
    private final T value;

    public ExistingOptional(final T value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Optional<T> filter(ThrowablePredicate<? super T> predicate) {
        try {
            return predicate.test(value) ? this : Optional.<T>empty();
        } catch (Exception e) {
            return Optional.exceptionThrown(e);
        }
    }

    @Override
    public <U> Optional<U> map(ThrowableFunction<? super T, ? extends U> mapper) {
        try {
            return Optional.ofNullable(mapper.apply(value));
        } catch (Exception e) {
            return Optional.exceptionThrown(e);
        }
    }

    @Override
    public <U> Optional<U> flatMap(ThrowableFunction<? super T, Optional<U>> mapper) {
        try {
            return mapper.apply(value);
        } catch (Exception e) {
            return Optional.exceptionThrown(e);
        }
    }

    @Override
    public Optional<T> each(ThrowableConsumer<? super T> consumer) {
        try {
            consumer.accept(value);
            return this;
        } catch (Exception e) {
            return Optional.exceptionThrown(e);
        }
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public T orNull() {
        return value;
    }

    @Override
    public T orElse(T other) {
        return value;
    }

    @Override
    public T orElseGet(Supplier<? extends T> other) {
        return value;
    }

    @Override
    public <E extends Throwable> T orElseThrow(E exceptionSupplier) throws E {
        return value;
    }

    @Override
    public <E extends Throwable> T orElseThrow(Supplier<? extends E> exceptionSupplier) throws E {
        return value;
    }

    @Override
    public Optional<T> exceptionally(Consumer<Throwable> exceptionOccurred) {
        return this;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExistingOptional<?> that = (ExistingOptional<?>) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
