package au.org.aodn.nrmn.restapi.validation.validators.format;

import lombok.val;

import java.util.List;

public class SurveyNumValidation extends IntegerFormatValidation {
    public SurveyNumValidation(List<Integer> validValues) {
        super(row -> {
          String[] split = row.getDepth().split("\\.");
          return  split[1];
        }, "Depth", validValues);
    }
}
