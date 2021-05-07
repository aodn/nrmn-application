package au.org.aodn.nrmn.restapi.validation.validators.global.raw;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ATRCMethodCheck extends BaseMethodCheckRaw {

    public ATRCMethodCheck() {
        super("ATRC Method check");
    }

    @Override
    protected boolean valid(List<String> methods) {
        return methods.containsAll(Arrays.asList("1", "2"))
                || methods.stream().anyMatch(s -> Arrays.asList("3", "4", "5").contains(s));
    }

}
