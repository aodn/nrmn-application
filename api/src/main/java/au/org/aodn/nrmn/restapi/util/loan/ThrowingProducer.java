package au.org.aodn.nrmn.restapi.util.loan;
@FunctionalInterface
public interface ThrowingProducer<T, E extends Exception> {
    T get() throws E;
}
