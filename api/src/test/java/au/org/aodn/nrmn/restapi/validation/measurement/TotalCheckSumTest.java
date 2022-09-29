package au.org.aodn.nrmn.restapi.validation.measurement;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.validation.process.FormattedTestProvider;

@ExtendWith(MockitoExtension.class)
class TotalCheckSumTest extends FormattedTestProvider  {

    @InjectMocks
    MeasurementValidation measurementValidation;

    @Test
    public void validSumShouldSuccess() {
        var row = getDefaultFormatted().build();
        row.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).build());
        row.setTotal(3);
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, row);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void validSumWithInvertsShouldSuccess() {
        var row = getDefaultFormatted().build();
        row.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).build());
        row.setInverts(2);
        row.setTotal(5);
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, row);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void InValidSumShouldFailed() {
        var row = getDefaultFormatted().build();
        row.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(0, 1).put(3, 1).put(4, 2).build());
        row.setTotal(3);
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, row);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void debrisZeroSumInvertsTotalZeroShouldSuccess() {
        var row = getDezFormatted().build();
        row.setInverts(0);
        row.setTotal(0);
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, row);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void debrisZeroSumInvertsTotalOneShouldSuccess() {
        var row = getDezFormatted().build();
        row.setInverts(1);
        row.setTotal(1);
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, row);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void debrisZeroSumInvertsTotalMismatchShouldFail() {
        var row = getDezFormatted().build();
        row.setInverts(0);
        row.setTotal(1);
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, row);
        assertFalse(errors.isEmpty());
    }
}
