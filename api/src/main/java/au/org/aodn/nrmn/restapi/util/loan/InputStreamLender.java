package au.org.aodn.nrmn.restapi.util.loan;

import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


public class InputStreamLender {
    public static <T> Optional<T> lend(
            ThrowingProducer<InputStream, Exception>  producer,
            ThrowingFunction<InputStream, T, Exception> consumer)
            throws Exception {
        AtomicReference<InputStream> in = new AtomicReference<>(null);
        AtomicReference<T> res = new AtomicReference<>(null);
        try {
            in.set(producer.get());
            res.set(consumer.apply(in.get()));
            in.get().close();
        } catch(Exception e) {

        } finally {
            if (in.get() != null)
                in.get().close();
        }
        return Optional.ofNullable(res.get());
    }
}
