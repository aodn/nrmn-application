package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.validation.validators.row.formatted.DebrisZeroObs;
import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DebrisZeroObsTest extends  FormattedTestProvider {

    @Test
    public void debrisWithNoValueShouldBeOK() {
        val formatted = getDefaultFormatted().build();
        val validationRule = new DebrisZeroObs();
        formatted.setCode("dez");
        formatted.setSpecies(ObservableItem.builder().observableItemName("Debris-Zero").build());
        formatted.setTotal(0);
        formatted.setInverts(0);
        formatted.setMeasureJson(Collections.emptyMap());
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }

    @Test
    public void debrisWith0ValueShouldBeOK() {
        val formatted = getDefaultFormatted().build();
        val validationRule = new DebrisZeroObs();
        formatted.setCode("dez");
        formatted.setSpecies(ObservableItem.builder().observableItemName("Debris-Zero").build());
        formatted.setTotal(0);
        formatted.setInverts(0);
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 0).build());
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }

    @Test
    public void NoDebris0ShouldBeOK() {
        val formatted = getDefaultFormatted().build();
        val validationRule = new DebrisZeroObs();
        formatted.setCode("123");
        formatted.setSpecies(ObservableItem.builder().observableItemName("SomethingElse").build());
        formatted.setTotal(0);
        formatted.setInverts(0);
        formatted.setMeasureJson(Collections.emptyMap());
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }


    @Test
    public void debris0WithInvertShouldFail() {
        val formatted = getDefaultFormatted().build();
        val validationRule = new DebrisZeroObs();
        formatted.setCode("dez");
        formatted.setSpecies(ObservableItem.builder().observableItemName("Debris-Zero").build());
        formatted.setTotal(10);
        formatted.setInverts(1);
        formatted.setMeasureJson(Collections.emptyMap());
        val res = validationRule.valid(formatted);
        assertTrue(res.isInvalid());
    }
}
