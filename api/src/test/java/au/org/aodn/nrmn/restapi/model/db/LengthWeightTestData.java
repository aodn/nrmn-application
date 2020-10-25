package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.LengthWeight.LengthWeightBuilder;
import org.springframework.stereotype.Component;

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
