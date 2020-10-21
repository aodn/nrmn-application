package au.org.aodn.nrmn.restapi.util.loan;

@FunctionalInterface
public interface ThrowingFunction<T,R, E extends Exception> {
    R apply(T t) throws E;
}