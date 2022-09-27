package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;

class Method3QuadratMax50Test extends FormattedTestProvider {
    @Test
    public void outOfScopeShouldSuccess() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(2);
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, formatted);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void method3WithQuadratsUnder50ShouldSuccess() {
        StagedRowFormatted row = getDefaultFormatted().build();
        row.setMethod(3);
        row.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 30);
                put(2, 20);
                put(3, 15);
                put(4, 20);
                put(5, 49);
            }
        });
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, row);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void method3WithQuadratsAbove50ShouldFailed() {
        StagedRowFormatted row = getDefaultFormatted().build();
        row.setMethod(3);
        row.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 100);
                put(2, 20);
                put(4, 20);
                put(5, 50);
            }
        });
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, row);
        assertTrue(errors.isEmpty());
    }
}
