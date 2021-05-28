package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.Method;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.validation.validators.row.formatted.SpeciesBelongToMethodCheck;
import com.google.common.collect.ImmutableSet;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesBelongToMethodCheckTest extends FormattedTestProvider{
    @Test
    public void matchingMethodShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        formatted.setMethod(1);
        formatted.setSpecies(Optional.of(
                ObservableItem.builder()
                              .observableItemName("THE SPECIES")
                              .methods(ImmutableSet.of(Method.builder().methodId(1).build()))
                              .build()));

        val validator = new SpeciesBelongToMethodCheck();
        val res =validator.valid(formatted);
        assertTrue(res.isValid());
    }

    @Test
    public void nonMatchingMethodShouldFail() {
        val formatted = getDefaultFormatted().build();
        formatted.setMethod(2);
        formatted.setSpecies(Optional.of(
                ObservableItem.builder()
                        .observableItemName("THE SPECIES")
                        .methods(ImmutableSet.of(Method.builder().methodId(1).build()))
                        .build()));

        val validator = new SpeciesBelongToMethodCheck();
        val res =validator.valid(formatted);
        assertTrue(res.isInvalid());
    }
}
