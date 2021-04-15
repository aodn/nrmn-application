package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesNotFoundTest extends FormattedTestProvider {
    @Test
    public void speciesNotFoundWithNoSizedShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        formatted.setSpecies(ObservableItem.builder().observableItemName("species not found").build());
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 0).put(4, 0).build());
        val validationRule = new SpeciesNotFound();
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }

    @Test
    public void speciesNotFoundWithSizedShouldFail() {
        val formatted = getDefaultFormatted().build();
        formatted.setSpecies(ObservableItem.builder().observableItemName("species Not found").build());
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 2).put(3, 1).put(4, 0).build());
        val validationRule = new SpeciesNotFound();
        val res = validationRule.valid(formatted);
        assertTrue(res.isInvalid());
    }
}