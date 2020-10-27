package au.org.aodn.nrmn.restapi.util;

@FunctionalInterface
public interface ConsumerThrowable<T, E extends Exception> {
   void apply(T input) throws E;
}
