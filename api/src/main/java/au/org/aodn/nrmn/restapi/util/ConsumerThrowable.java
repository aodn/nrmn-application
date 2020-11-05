package au.org.aodn.nrmn.restapi.util;

@FunctionalInterface
public interface ConsumerThrowable<T, R, E extends Exception> {
   R apply(T input) throws E;
}

