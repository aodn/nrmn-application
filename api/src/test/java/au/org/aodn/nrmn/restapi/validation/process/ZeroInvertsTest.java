package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.service.validation.SiteValidation;
import au.org.aodn.nrmn.restapi.service.validation.SurveyValidation;

class ZeroInvertsTest extends FormattedTestProvider {

    @Mock
    MeasurementValidation measurementValidation;
    
    @Mock
    SiteValidation siteValidation;

    @InjectMocks 
    SurveyValidation surveyValidation;
    
    @Test
    void method3WithInvertsShouldSucceed() {
        var formatted = getDefaultFormatted().build();
        formatted.setMethod(3);
        formatted.setInverts(0);
        var error = surveyValidation.validateInvertsZeroOnM3M4M5(formatted);
        assertNull(error);
    }

    @Test
    void method3WithInvertsShouldFail() {
        var formatted = getDefaultFormatted().build();
        formatted.setMethod(3);
        formatted.setInverts(5);
        var error = surveyValidation.validateInvertsZeroOnM3M4M5(formatted);
        assertNotNull(error);
        assertTrue(error.getMessage().contains("Method 3"));
    }

    @Test
    void method4WithInvertsShouldFail() {
        var formatted = getDefaultFormatted().build();
        formatted.setMethod(4);
        formatted.setInverts(5);
        var error = surveyValidation.validateInvertsZeroOnM3M4M5(formatted);
        assertNotNull(error);
        assertTrue(error.getMessage().contains("Method 4"));
    }

    @Test
    void method5WithInvertsShouldFail() {
        var formatted = getDefaultFormatted().build();
        formatted.setMethod(5);
        formatted.setInverts(5);
        var error = surveyValidation.validateInvertsZeroOnM3M4M5(formatted);
        assertNotNull(error);
        assertTrue(error.getMessage().contains("Method 5"));
    }
}
