package au.org.aodn.nrmn.restapi.validation.validators.global;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RLSMethodCheck extends BaseMethodCheck {

    public RLSMethodCheck() {
        super("RLS Method check");
    }

    @Override
    protected boolean valid(List<String> methods) {
        return methods.containsAll(Arrays.asList("1", "2"));
    }

}
