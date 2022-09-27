package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

class Method3QuadratsMissingTest extends FormattedTestProvider {

    @Test
    void transectWithAllQuadratsFilledShouldSuccess() {
        StagedRowFormatted r1 = getDefaultFormatted().build();
        r1.setMethod(3);
        r1.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 4);
                put(2, 2);
            }
        });
        StagedRowFormatted r2 = getDefaultFormatted().build();
        r2.setMethod(3);
        r2.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(4, 1);
                put(3, 2);
            }
        });
        StagedRowFormatted r3 = getDefaultFormatted().build();
        r3.setMethod(3);
        r3.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(5, 9);
            }
        });

        SurveyValidationError error = validationProcess.validateMethod3Quadrats("0", Arrays.asList(r1, r2, r3));
        assertTrue(error == null);
    }

    @Test
    void transectWithMissingQuadratsFilledShouldSuccess() {
        StagedRowFormatted r1 = getDefaultFormatted().build();
        r1.setMethod(3);
        r1.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 4);
                put(2, 2);
            }
        });
        StagedRowFormatted r2 = getDefaultFormatted().build();
        r2.setMethod(3);
        r2.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(4, 1);
                put(3, 2);
            }
        });
        
        SurveyValidationError error = validationProcess.validateMethod3Quadrats("0", Arrays.asList(r1, r2));
        assertTrue(error != null);
    }
}
