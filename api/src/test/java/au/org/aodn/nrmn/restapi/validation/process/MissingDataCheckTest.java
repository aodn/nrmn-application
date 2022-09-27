package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import au.org.aodn.nrmn.db.model.ObsItemType;
import au.org.aodn.nrmn.db.model.ObservableItem;
import au.org.aodn.nrmn.db.model.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;

class MissingDataCheckTest extends FormattedTestProvider {
    @Test
    public void noSpeciesFoundWithNoObservationsShouldSucceed() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().build());
        formatted.setInverts(0);
        formatted.setTotal(0);
        formatted.setCode("nsf");

        formatted.setSpecies(
                Optional.of(ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(6).build())
                        .observableItemName("No Species Found").build()));

        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, formatted);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void sndWithNoObservationsShouldSucceed() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().build());
        formatted.setInverts(0);
        formatted.setTotal(0);
        formatted.setCode("snd");
        formatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("Survey Not Done").build()));
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, formatted);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void speciesWithInvertsShouldSucceed() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().build());
        formatted.setInverts(4);
        formatted.setCode("pla");
        formatted.setSpecies(
                Optional.of(ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(1).build())
                        .observableItemName("Pictilabrus laticlavius").letterCode("pla").build()));
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, formatted);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void speciesWithNoObservationsShouldFail() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().build());
        formatted.setTotal(0);
        formatted.setCode("pla");
        formatted.setSpecies(
                Optional.of(ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(1).build())
                        .observableItemName("Pictilabrus laticlavius").letterCode("pla").build()));
        Collection<ValidationCell> errors = validationProcess.validateMeasurements(ProgramValidation.RLS, formatted);
        assertFalse(errors.isEmpty());
    }
}
