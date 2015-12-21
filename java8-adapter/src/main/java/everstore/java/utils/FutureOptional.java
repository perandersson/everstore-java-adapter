package everstore.java.utils;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class FutureOptional<T> implements Optional<T> {

    private final CompletableFuture<T> future;

    public FutureOptional(final CompletableFuture<T> future) {
        this.future = Objects.requireNonNull(future);
    }

    @Override
    public Optional<T> filter(ThrowablePredicate<? super T> predicate) {
        final CompletableFuture<T> resultFuture = new CompletableFuture<>();
        future.thenAcceptAsync(value -> {
            try {
                if (predicate.test(value)) {
                    resultFuture.complete(value);
                } else {
                    resultFuture.cancel(false);
                }
            } catch (Exception e) {
                resultFuture.completeExceptionally(e);
            }
        }).exceptionally(t -> {
            resultFuture.completeExceptionally(t);
            return null;
        });
        return Optional.of(resultFuture);
    }

    @Override
    public <U> Optional<U> map(ThrowableFunction<? super T, ? extends U> mapper) {
        final CompletableFuture<U> resultFuture = new CompletableFuture<>();
        future.thenAcceptAsync(val -> {
            try {
                resultFuture.complete(mapper.apply(val));
            } catch (Exception e) {
                resultFuture.completeExceptionally(e);
            }
        }).exceptionally(t -> {
            resultFuture.completeExceptionally(t);
            return null;
        });
        return Optional.of(resultFuture);
    }

    @Override
    public <U> Optional<U> flatMap(ThrowableFunction<? super T, Optional<U>> mapper) {
        final CompletableFuture<U> resultFuture = new CompletableFuture<>();
        future.thenAcceptAsync(val -> {
            try {
                final Optional<U> mappedResult = mapper.apply(val);
                mappedResult.each(resultFuture::complete).exceptionally(resultFuture::completeExceptionally);
            } catch (Exception e) {
                resultFuture.completeExceptionally(e);
            }
        }).exceptionally(t -> {
            resultFuture.completeExceptionally(t);
            return null;
        });
        return Optional.of(resultFuture);
    }

    @Override
    public Optional<T> each(ThrowableConsumer<? super T> consumer) {
        final CompletableFuture<T> resultFuture = new CompletableFuture<>();
        future.thenAcceptAsync(val -> {
            try {
                consumer.accept(val);
                resultFuture.complete(val);
            } catch (Exception e) {
                resultFuture.completeExceptionally(e);
            }
        }).exceptionally(t -> {
            resultFuture.completeExceptionally(t);
            return null;
        });
        return Optional.of(resultFuture);
    }

    @Override
    public boolean isPresent() {
        return future.isDone() && !future.isCompletedExceptionally() && future.getNow(null) != null;
    }

    @Override
    public boolean isEmpty() {
        return future.isDone() && future.isCompletedExceptionally() && future.getNow(null) == null;
    }

    @Override
    public T get() {
        try {
            return future.get();
        } catch (final InterruptedException e) {
            throw new IllegalStateException("Value could not be resolved from future", e);
        } catch (final ExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new IllegalStateException("Value could not be resolved from future", e.getCause());
            }
        }
    }

    @Override
    public T orNull() {
        try {
            return future.get();
        } catch (final InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public T orElse(T other) {
        try {
            final T value = future.get();
            return value != null ? value : other;
        } catch (final InterruptedException | ExecutionException e) {
            return other;
        }
    }

    @Override
    public T orElseGet(Supplier<? extends T> other) {
        try {
            final T value = future.get();
            return value != null ? value : other.get();
        } catch (final InterruptedException | ExecutionException e) {
            return other.get();
        }
    }

    @Override
    public <E extends Throwable> T orElseThrow(E exceptionSupplier) throws E {
        try {
            final T value = future.get();
            if (value != null) return value;
            else throw exceptionSupplier;
        } catch (final InterruptedException | ExecutionException e) {
            throw exceptionSupplier;
        }
    }

    @Override
    public <E extends Throwable> T orElseThrow(Supplier<? extends E> exceptionSupplier) throws E {
        try {
            final T value = future.get();
            if (value != null) return value;
            else throw exceptionSupplier.get();
        } catch (final InterruptedException | ExecutionException e) {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public Optional<T> exceptionally(final Consumer<Throwable> exceptionOccurred) {
        future.exceptionally(t -> {
            exceptionOccurred.accept(t);
            return null;
        });
        return this;
    }
}
