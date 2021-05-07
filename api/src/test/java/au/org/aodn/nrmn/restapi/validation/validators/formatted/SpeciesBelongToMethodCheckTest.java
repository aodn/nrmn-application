package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesBelongToMethodCheckTest extends FormattedTestProvider{
    @Test
    public void matchingMethodShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        formatted.setMethod(1);
        formatted.setSpecies(
                ObservableItem.builder()
                        .obsItemAttribute(ImmutableMap.<String, String>builder()
                                .put("is_method", "1").build())
                        .observableItemName("THE SPECIES").build());

        val validator = new SpeciesBelongToMethodCheck();
        val res =validator.valid(formatted);
        assertTrue(res.isValid());
    }

    @Test
    public void nonMatchingMethodShouldFail() {
        val formatted = getDefaultFormatted().build();
        formatted.setMethod(2);
        formatted.setSpecies(
                ObservableItem.builder()
                        .obsItemAttribute(ImmutableMap.<String, String>builder()
                                .put("is_method", "1").build())
                        .observableItemName("THE SPECIES").build());

        val validator = new SpeciesBelongToMethodCheck();
        val res =validator.valid(formatted);
        assertTrue(res.isInvalid());
    }
}
