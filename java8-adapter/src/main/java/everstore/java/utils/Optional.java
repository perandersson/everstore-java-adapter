package everstore.java.utils;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Optional<T> extends Serializable {
    Optional EMPTY = new Empty();

    Optional<T> filter(ThrowablePredicate<? super T> predicate);

    <U> Optional<U> map(ThrowableFunction<? super T, ? extends U> mapper);

    <U> Optional<U> flatMap(ThrowableFunction<? super T, Optional<U>> mapper);

    Optional<T> each(ThrowableConsumer<? super T> consumer);

    //<E extends Throwable> Optional<T> exception(Consumer<? extends E> consumer);

    boolean isPresent();

    boolean isEmpty();

    T get();

    T orNull();

    T orElse(T other);

    T orElseGet(Supplier<? extends T> other);

    <E extends Throwable> T orElseThrow(E e) throws E;

    <E extends Throwable> T orElseThrow(Supplier<? extends E> exceptionSupplier) throws E;

    Optional<T> exceptionally(Consumer<Throwable> exceptionOccurred);

    static <T, E extends Throwable> Optional<T> exceptionThrown(E exception) {
        return new ExceptionOptional<>(exception);
    }

    static <T> Optional<T> empty() {
        return EMPTY;
    }

    static <T> Optional<T> of(T value) {
        return new ExistingOptional<>(value);
    }

    static <T> Optional<T> of(CompletableFuture<T> fut) {
        return new FutureOptional<>(fut);
    }

    static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    static <T> Optional<T> ofNullable(CompletableFuture<T> fut) {
        return fut == null ? empty() : of(fut);
    }

}
