package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;

class Method3QuadratsSumTest extends FormattedTestProvider {
    @Test
    void quadratsSumUnder50ShouldFail() {
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
                put(3, 8);
                put(50, 5);
            }
        });

        StagedRowFormatted r3 = getDefaultFormatted().build();
        r3.setMethod(3);
        r3.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(3, 4);
        
            }
        });

        StagedRowFormatted r4 = getDefaultFormatted().build();
        r4.setMethod(3);
        r4.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 4);
                put(3, 7);
            }
        });
        LocalDate date = LocalDate.now();

        StagedRowFormatted a1 = getDefaultFormatted().build();

        a1.setMethod(3);
        a1.setDate(date);
        a1.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 4);
                put(3, 7);
            }
        });

        StagedRowFormatted a2 = getDefaultFormatted().build();
        a2.setMethod(3);
        a2.setDate(date);
        a2.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(2,3);
            }
        });

        StagedRowFormatted a3 = getDefaultFormatted().build();
        a3.setMethod(3);
        a3.setDate(date);
        a3.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(4,10);
            }
        });

        StagedRowFormatted a4 = getDefaultFormatted().build();
        a4.setMethod(3);
        a4.setDate(date);
        a4.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(5,6);
            }
        });

        SurveyValidationError error = validationProcess.validateMethod3QuadratsGT50("", Arrays.asList(r1, r2, r3, r4, a1, a2, a3, a4));
        assertTrue(error != null && error.getMessage().startsWith("Quadrats do not sum to at least 50 in transect"));
    }

    @Test
    void quadratValueOver50ShouldFail() {
        StagedRowFormatted r1 = getDefaultFormatted().build();
        r1.setMethod(3);
        r1.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 40);
                put(2, 20);
            }
        });

        StagedRowFormatted r2 = getDefaultFormatted().build();
        r2.setMethod(3);
        r2.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(3, 80);
                put(4, 45);
                put(5, 30);
            }
        });

        StagedRowFormatted r3 = getDefaultFormatted().build();
        r3.setMethod(3);
        r3.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(2, 10);
                put(4, 6);
            }
        });

        StagedRowFormatted r4 = getDefaultFormatted().build();
        r4.setMethod(3);
        r4.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 140);
                put(2, 20);
                put(3, 70);
            }
        });
        LocalDate date = LocalDate.now();

        StagedRowFormatted a1 = getDefaultFormatted().build();

        a1.setMethod(3);
        a1.setDate(date);
        a1.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 42);
                put(3, 70);
            }
        });

        StagedRowFormatted a2 = getDefaultFormatted().build();
        a2.setMethod(3);
        a2.setDate(date);
        a2.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 10);
                put(2, 35);
            }
        });

        StagedRowFormatted a3 = getDefaultFormatted().build();
        a3.setMethod(3);
        a3.setDate(date);
        a3.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(2,16);
                put(4,100);
                put(5,52);
            }
        });

        StagedRowFormatted a4 = getDefaultFormatted().build();
        a4.setMethod(3);
        a4.setDate(date);
        a4.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(5,6);
            }
        });

        Collection<ValidationCell> errors = validationProcess.validateMethod3QuadratsLT50(Arrays.asList(r1, r2, r3, r4, a1, a2, a3, a4));
        assertTrue(errors != null && errors.iterator().next().getMessage().startsWith("M3 quadrat more than 50"));
    }

    @Test
    void quadratsSumUnder50ShouldSuccess() {
        StagedRowFormatted r1 = getDefaultFormatted().build();
        r1.setMethod(3);
        r1.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 40);
                put(2, 20);
            }
        });

        StagedRowFormatted r2 = getDefaultFormatted().build();
        r2.setMethod(3);
        r2.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(3, 80);
                put(4, 45);
                put(5, 30);
            }
        });

        StagedRowFormatted r3 = getDefaultFormatted().build();
        r3.setMethod(3);
        r3.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(2, 10);
                put(4, 6);
            }
        });

        StagedRowFormatted r4 = getDefaultFormatted().build();
        r4.setMethod(3);
        r4.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 140);
                put(2, 20);
                put(3, 70);
            }
        });
        LocalDate date = LocalDate.now();

        StagedRowFormatted a1 = getDefaultFormatted().build();

        a1.setMethod(3);
        a1.setDate(date);
        a1.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 42);
                put(3, 70);
            }
        });

        StagedRowFormatted a2 = getDefaultFormatted().build();
        a2.setMethod(3);
        a2.setDate(date);
        a2.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 10);
                put(2, 35);
            }
        });

        StagedRowFormatted a3 = getDefaultFormatted().build();
        a3.setMethod(3);
        a3.setDate(date);
        a3.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(2,16);
                put(4,100);
                put(5,52);
            }
        });

        StagedRowFormatted a4 = getDefaultFormatted().build();
        a4.setMethod(3);
        a4.setDate(date);
        a4.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(5,6);
            }
        });

        SurveyValidationError error = validationProcess.validateMethod3QuadratsGT50("", Arrays.asList(r1, r2, r3, r4, a1, a2, a3, a4));
        assertFalse(error != null && error.getMessage().startsWith("Quadrats do not sum to at least 50 in transect"));
    }
}
