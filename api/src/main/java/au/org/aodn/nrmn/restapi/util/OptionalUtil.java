package au.org.aodn.nrmn.restapi.util;

import java.util.Optional;
import java.util.stream.Stream;

public class OptionalUtil {

    static public <T> Stream<T> toStream(Optional<T> opt) {
        return opt.map(Stream::of).orElseGet(Stream::empty);
    }
}
