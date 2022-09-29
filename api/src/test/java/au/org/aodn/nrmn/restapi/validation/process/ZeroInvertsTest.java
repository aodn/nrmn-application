package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;

class ZeroInvertsTest extends FormattedTestProvider {

    @Mock
    MeasurementValidation measurementValidation;
    
    @Test
    void method3WithInvertsShouldSucceed() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(3);
        formatted.setInverts(0);
        Collection<SurveyValidationError> errors = validationProcess.checkData(ProgramValidation.ATRC, false, Arrays.asList(formatted));
        assertFalse(errors.stream().anyMatch(p -> p.getMessage().contains("Method 3")));
    }

    @Test
    void method3WithInvertsShouldFail() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(3);
        formatted.setInverts(5);
        Collection<SurveyValidationError> errors = validationProcess.checkData(ProgramValidation.ATRC, false, Arrays.asList(formatted));
        assertTrue(errors.stream().anyMatch(p -> p.getMessage().contains("Method 3")));
    }

    @Test
    void method4WithInvertsShouldFail() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(4);
        formatted.setInverts(5);
        Collection<SurveyValidationError> errors = validationProcess.checkData(ProgramValidation.ATRC, false, Arrays.asList(formatted));
        assertTrue(errors.stream().anyMatch(p -> p.getMessage().contains("Method 4")));
    }

    @Test
    void method5WithInvertsShouldFail() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(5);
        formatted.setInverts(5);
        Collection<SurveyValidationError> errors = validationProcess.checkData(ProgramValidation.ATRC, false, Arrays.asList(formatted));
        assertTrue(errors.stream().anyMatch(p -> p.getMessage().contains("Method 5")));
    }
}
