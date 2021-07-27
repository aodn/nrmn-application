package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.Method;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
// import au.org.aodn.nrmn.restapi.validation.validators.row.formatted.SpeciesBelongToMethodCheck;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesBelongToMethodCheckTest extends FormattedTestProvider{
    @Test
    public void matchingMethodShouldSuccess() {
        final Set<Method> methods = new HashSet<Method>();
        methods.add(Method.builder().methodId(1).build());
        val formatted = getDefaultFormatted().build();
        formatted.setMethod(1);
        formatted.setSpecies(Optional.of(
                ObservableItem.builder()
                              .observableItemName("THE SPECIES")
                              .methods(methods)
                              .build()));

        // val validator = new SpeciesBelongToMethodCheck();
        // val res =validator.valid(formatted);
        // assertTrue(res.isValid());
    }

    @Test
    public void nonMatchingMethodShouldFail() {
        final Set<Method> methods = new HashSet<Method>();
        methods.add(Method.builder().methodId(1).build());
        val formatted = getDefaultFormatted().build();
        formatted.setMethod(2);
        formatted.setSpecies(Optional.of(
                ObservableItem.builder()
                        .observableItemName("THE SPECIES")
                        .methods(methods)
                        .build()));

        // val validator = new SpeciesBelongToMethodCheck();
        // val res =validator.valid(formatted);
        // assertTrue(res.isInvalid());
    }
}
