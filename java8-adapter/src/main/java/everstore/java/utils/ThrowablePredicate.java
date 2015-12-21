package everstore.java.utils;

@FunctionalInterface
public interface ThrowablePredicate<T> {
    boolean test(T t) throws Exception;
}
