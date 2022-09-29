package au.org.aodn.nrmn.restapi.model.db;

import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.LengthWeight;
import au.org.aodn.nrmn.restapi.data.model.LengthWeight.LengthWeightBuilder;

@Component
public class LengthWeightTestData {
    public LengthWeightBuilder defaultBuilder() {
        return LengthWeight.builder()
            .a(0.0281)
            .b(2.875)
            .cf(1.0)
            .sgfgu("F");
    }
}
