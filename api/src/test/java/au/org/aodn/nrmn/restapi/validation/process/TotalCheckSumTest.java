package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;

@ExtendWith(MockitoExtension.class)
class TotalCheckSumTest extends FormattedTestProvider  {

    @InjectMocks
    ValidationProcess validationProcess;

    @Test
    public void validSumShouldSuccess() {
        StagedRowFormatted row = getDefaultFormatted().build();
        row.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).build());
        row.setTotal(3);
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, row);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void validSumWithInvertsShouldSuccess() {
        StagedRowFormatted row = getDefaultFormatted().build();
        row.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).build());
        row.setInverts(2);
        row.setTotal(5);
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, row);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void InValidSumShouldFailed() {
        StagedRowFormatted row = getDefaultFormatted().build();
        row.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(0, 1).put(3, 1).put(4, 2).build());
        row.setTotal(3);
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, row);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void debrisZeroSumInvertsTotalZeroShouldSuccess() {
        StagedRowFormatted row = getDezFormatted().build();
        row.setInverts(0);
        row.setTotal(0);
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, row);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void debrisZeroSumInvertsTotalOneShouldSuccess() {
        StagedRowFormatted row = getDezFormatted().build();
        row.setInverts(1);
        row.setTotal(1);
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, row);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void debrisZeroSumInvertsTotalMismatchShouldFail() {
        StagedRowFormatted row = getDezFormatted().build();
        row.setInverts(0);
        row.setTotal(1);
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, row);
        assertFalse(errors.isEmpty());
    }
}
