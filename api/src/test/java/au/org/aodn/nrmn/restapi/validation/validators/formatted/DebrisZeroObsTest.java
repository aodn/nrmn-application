package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

class DebrisZeroObsTest extends FormattedTestProvider {

    @Test
    public void debrisWithNoValueShouldBeOK() {
        ObservableItem oi = ObservableItem.builder().observableItemName("Debris-Zero").build();
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setCode("dez");
        formatted.setSpecies(Optional.of(oi));
        formatted.setTotal(0);
        formatted.setInverts(0);
        formatted.setMeasureJson(Collections.emptyMap());
        Collection<ValidationCell> errors = validationProcess.validateMeasurements("RLS", formatted);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void debrisWith0ValueShouldBeOK() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setCode("dez");
        formatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("Debris-Zero").build()));
        formatted.setTotal(0);
        formatted.setInverts(0);
        formatted.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 0);
                put(3, 0);
            }
        });
        Collection<ValidationCell> errors = validationProcess.validateMeasurements("RLS", formatted);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void DebrisTotalInvertsEqual1ShouldBeOK() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setCode("dez");
        formatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("Debris-Zero").build()));
        formatted.setTotal(1);
        formatted.setInverts(1);
        formatted.setMeasureJson(Collections.emptyMap());
        Collection<ValidationCell> errors = validationProcess.validateMeasurements("RLS", formatted);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void DebrisTotalInvertsNot1ShouldFail() {
        Collection<ValidationCell> errors;
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setCode("dez");
        formatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("Debris-Zero").build()));
        formatted.setMeasureJson(Collections.emptyMap());

        formatted.setTotal(0);
        formatted.setInverts(1);
        errors = validationProcess.validateMeasurements("RLS", formatted);
        assertTrue(errors.stream().anyMatch(e -> e.getLevelId() == ValidationLevel.BLOCKING));

        formatted.setTotal(1);
        formatted.setInverts(0);
        errors = validationProcess.validateMeasurements("RLS", formatted);
        assertTrue(errors.stream().anyMatch(e -> e.getLevelId() == ValidationLevel.BLOCKING));
    }
}
