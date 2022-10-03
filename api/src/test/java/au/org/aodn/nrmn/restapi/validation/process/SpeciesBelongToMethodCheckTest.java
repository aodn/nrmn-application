package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import au.org.aodn.nrmn.restapi.data.model.Method;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.service.validation.SiteValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.service.validation.SurveyValidation;

class SpeciesBelongToMethodCheckTest extends FormattedTestProvider {
    
    @Mock
    MeasurementValidation measurementValidation;

    @Mock
    SiteValidation siteValidation;

    @InjectMocks
    SurveyValidation surveyValidation;

    @Test
    public void matchingMethodShouldSuccess() {
        final Set<Method> methods = new HashSet<Method>();
        methods.add(Method.builder().methodId(1).build());
        var species = ObservableItem.builder().observableItemName("THE SPECIES").methods(methods).build();
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(1);
        formatted.setSpecies(Optional.of(species));
        var error = surveyValidation.validateSpeciesBelowToMethod(formatted);
        assertNull(error);
    }

    @Test
    public void nonMatchingMethodShouldFail() {
        final Set<Method> methods = new HashSet<Method>();
        methods.add(Method.builder().methodId(1).build());
        var species = ObservableItem.builder().observableItemName("THE SPECIES").methods(methods).build();
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(2);
        formatted.setSpecies(Optional.of(species));
        var error = surveyValidation.validateSpeciesBelowToMethod(formatted);
        assertNotNull(error);
        assertTrue(error.getMessage().contains("invalid for species"));
    }
}
