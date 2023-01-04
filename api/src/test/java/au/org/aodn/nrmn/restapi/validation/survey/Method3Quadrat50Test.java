package au.org.aodn.nrmn.restapi.validation.survey;

import au.org.aodn.nrmn.restapi.data.model.Method;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.service.validation.SurveyValidation;
import au.org.aodn.nrmn.restapi.validation.process.FormattedTestProvider;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class Method3Quadrat50Test extends FormattedTestProvider {

    @InjectMocks
    SurveyValidation surveyValidation;

    /**
     * Add up all values in the measure json for each method in the list is larger than zero
     */
    @Test
    public void method3WithQuadratsNotZero() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(3);
        formatted.setMeasureJson(new HashMap<>() {
            {
                put(1, 30);
                put(2, 20);
                put(3, 15);
                put(4, 20);
                put(5, 49);
            }
        });

        SurveyValidationError error = surveyValidation.validateMethod3Quadrats("any_value", Arrays.asList(formatted));
        assertNull("No error found", error);

        // Now we have two objects in the list where method 1 add up to zero
        StagedRowFormatted formatted1 = getDefaultFormatted().build();
        formatted1.setMethod(3);
        formatted1.setMeasureJson(new HashMap<>() {
            {
                put(1, 0);
                put(2, 30);
            }
        });

        error = surveyValidation.validateMethod3Quadrats("any_value1", Arrays.asList(formatted1, formatted1));
        assertNotNull("Should have error", error);
        assertEquals("One error found", error.getLevelId(), ValidationLevel.BLOCKING);

        // Lastly if method is not 3 then validation will not have error
        StagedRowFormatted formatted2 = getDefaultFormatted().build();
        formatted2.setMethod(2);
        formatted2.setMeasureJson(new HashMap<>() {
            {
                put(1, 0);
                put(2, 30);
            }
        });
        error = surveyValidation.validateMethod3Quadrats("any_value2", Arrays.asList(formatted2, formatted2));
        assertNull("Method is not 3 and hence no error", error);
    }

    /**
     * Validate any measurement 1, 2, 3.... 5 individually in the list is less than 50, if yes report error
     */
    @Test
    public void method3WithQuadratsLT50() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(3);
        formatted.setMeasureJson(new HashMap<>() {
            {
                put(1, 30);
                put(2, 20);
                put(3, 15);
                put(4, 50);
                put(5, 51);
            }
        });

        Collection<ValidationCell> error = surveyValidation.validateMethod3QuadratsLT50(Arrays.asList(formatted, formatted));
        assertEquals("Found 2 error", error.size(), 2);

        // Now if it is not method 3 then no error return
        formatted.setMethod(2);
        error = surveyValidation.validateMethod3QuadratsLT50(Arrays.asList(formatted, formatted));
        assertEquals("No error found", error.size(), 0);
    }

    /**
     * Sum up the values in the list by grouping it under the same method id (1, 2... 5) should be greater than 50
     * otherwise issue error.
     */
    @Test
    public void method3WithQuadratsGT50() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(3);
        formatted.setMeasureJson(new HashMap<>() {
            {
                put(1, 30);
                put(2, 30);
                put(3, 35);
                put(4, 50);
                put(5, 51);
            }
        });

        SurveyValidationError error = surveyValidation.validateMethod3QuadratsGT50("any_value_is_ok", Arrays.asList(formatted, formatted));
        assertNull("No error", error);

        // Should have 1 method that do not add > 50
        formatted.setMeasureJson(new HashMap<>() {
            {
                put(1, 30);
                put(2, 30);
                put(3, 15);
                put(4, 25);
                put(5, 51);
            }
        });
        error = surveyValidation.validateMethod3QuadratsGT50("any_value_is_ok", Arrays.asList(formatted, formatted));
        assertNotNull("One error found", error);

        // If it is method two then out of scope of this method and hence no error
        formatted.setMethod(2);
        error = surveyValidation.validateMethod3QuadratsGT50("any_value_is_ok", Arrays.asList(formatted, formatted));
        assertNull("One error found", error);
    }
    /**
     * SurveyNum 0-4 must appear in the list
     */
    @Test
    public void validateSurveyTransectNumber() {
        // Missing survey number will result in error
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(3);
        formatted.setSurveyNum(null);
        formatted.setMeasureJson(new HashMap<>() {
            {
                put(1, 30);
            }
        });

        SurveyValidationError error = surveyValidation.validateSurveyTransectNumber(Arrays.asList(formatted, formatted));
        assertNotNull("One error found", error);

        // Set number not belongs to 0-4 is also an error
        formatted.setSurveyNum(5);
        error = surveyValidation.validateSurveyTransectNumber(Arrays.asList(formatted, formatted));
        assertNotNull("One error found", error);

        // No error if SurveyNum within 0-4
        formatted.setSurveyNum(3);
        error = surveyValidation.validateSurveyTransectNumber(Arrays.asList(formatted, formatted));
        assertNull("No error found", error);
    }
    /**
     *
     */
    @Test
    public void validateDateRange() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setDate(null);

        ValidationCell error = surveyValidation.validateDateRange(ProgramValidation.RLS, formatted);
        assertNull("Date is null, so no error?", error);

        // Future date not allowed
        formatted.setDate(LocalDate.from(ZonedDateTime.now().plusDays(1)));
        error = surveyValidation.validateDateRange(ProgramValidation.RLS, formatted);
        assertNotNull("Future date, so error", error);

        // Cannot smaller than the program min date
        formatted.setDate(LocalDate.from(ProgramValidation.RLS.getMinDate().minusDays(1)));
        error = surveyValidation.validateDateRange(ProgramValidation.RLS, formatted);
        assertNotNull("Smaller than RLS min date, so error", error);

        formatted.setDate(LocalDate.from(ProgramValidation.ATRC.getMinDate().minusDays(1)));
        error = surveyValidation.validateDateRange(ProgramValidation.ATRC, formatted);
        assertNotNull("Smaller than ATRC min date, so error", error);

        // No error if within range
        formatted.setDate(LocalDate.from(ProgramValidation.RLS.getMinDate().plusDays(1)));
        error = surveyValidation.validateDateRange(ProgramValidation.RLS, formatted);
        assertNull("Greater than RLS min date, so no error", error);

        formatted.setDate(LocalDate.from(ProgramValidation.ATRC.getMinDate().plusDays(1)));
        error = surveyValidation.validateDateRange(ProgramValidation.ATRC, formatted);
        assertNull("Greater than ATRC min date, so no error", error);
    }
    /**
     * The invert field must be zero for method 3-5
     */
    @Test
    public void validateInvertsZeroOnM3M4M5() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(3);
        formatted.setInverts(1);

        // Cannot greater than zero for method 3
        ValidationCell error = surveyValidation.validateInvertsZeroOnM3M4M5(formatted);
        assertNotNull("Inverts is 1 so error", error);

        // Zero is ok
        formatted.setInverts(0);
        error = surveyValidation.validateInvertsZeroOnM3M4M5(formatted);
        assertNull("Inverts is 0 no error", error);

        // Can greater than zero for method 1
        formatted.setMethod(1);
        error = surveyValidation.validateInvertsZeroOnM3M4M5(formatted);
        assertNull("Inverts is 1 no error", error);
    }
    /**
     * Verify method as specified for the species matches the method use in the stagged row, if not
     * issue error
     */
    @Test
    public void validateSpeciesBelowToMethod() {
        StagedRowFormatted stagged = getDefaultFormatted().build();

        ValidationCell error = surveyValidation.validateSpeciesBelowToMethod(Boolean.FALSE, stagged);
        assertNull("Null return because species method is null", error);

        Method method1 = new Method();
        method1.setMethodId(1);

        // Species method not null
        stagged.setMethod(2);
        stagged.getSpecies().get().setMethods(Set.of(method1));
        error = surveyValidation.validateSpeciesBelowToMethod(Boolean.FALSE, stagged);
        assertNotNull("Stagged method do not appear in species method list", error);

        stagged.setMethod(1);
        error = surveyValidation.validateSpeciesBelowToMethod(Boolean.FALSE, stagged);
        assertNull("Stagged method match, so no error", error);

        stagged.setMethod(10);
        error = surveyValidation.validateSpeciesBelowToMethod(Boolean.FALSE, stagged);
        assertNull("Method 10 in row will be treat as method 1 in species, so no error", error);

        // Now test the allowM11 flag
        stagged.setMethod(11);
        error = surveyValidation.validateSpeciesBelowToMethod(Boolean.FALSE, stagged);
        assertNotNull("Do not allow method 11 so error if row method 11", error);

        error = surveyValidation.validateSpeciesBelowToMethod(Boolean.TRUE, stagged);
        assertNull("Allow method 11 so no error is user set method 11", error);

        stagged.setMethod(2);
        error = surveyValidation.validateSpeciesBelowToMethod(Boolean.TRUE, stagged);
        assertNotNull("Allow method 11 with method 2, it should yield error", error);
    }
}
