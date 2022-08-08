package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.Method;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpeciesBelongToMethodCheckTest extends FormattedTestProvider {

    @Test
    public void matchingMethodShouldSuccess() {
        final Set<Method> methods = new HashSet<Method>();
        methods.add(Method.builder().methodId(1).build());
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(1);
        formatted.setSpecies(
                Optional.of(ObservableItem.builder().observableItemName("THE SPECIES").methods(methods).build()));

        Collection<ValidationError> errors = validationProcess.checkData(ProgramValidation.ATRC, false, Arrays.asList(formatted));
        assertFalse(errors.stream().anyMatch(p -> p.getMessage().contains("invalid for species")));
    }

    @Test
    public void nonMatchingMethodShouldFail() {
        final Set<Method> methods = new HashSet<Method>();
        methods.add(Method.builder().methodId(1).build());
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(2);
        formatted.setSpecies(
                Optional.of(ObservableItem.builder().observableItemName("THE SPECIES").methods(methods).build()));

        Collection<ValidationError> errors = validationProcess.checkData(ProgramValidation.ATRC, false, Arrays.asList(formatted));
        assertTrue(errors.stream().anyMatch(p -> p.getMessage().contains("invalid for species")));
    }
}
