package au.org.aodn.nrmn.restapi.validation.measurement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import au.org.aodn.nrmn.restapi.data.model.ObsItemType;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.process.FormattedTestProvider;

class MissingDataCheckTest extends FormattedTestProvider {
    
    @InjectMocks
    MeasurementValidation measurementValidation;
    
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

        Collection<ValidationCell> errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, formatted);
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
        Collection<ValidationCell> errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, formatted);
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
        Collection<ValidationCell> errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, formatted);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void speciesWithNoObservationsShouldFail() {
        var formatted = getDefaultFormatted().build();
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().build());
        formatted.setTotal(0);
        formatted.setCode("pla");
        formatted.setSpecies(
                Optional.of(ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(1).build())
                        .observableItemName("Pictilabrus laticlavius").letterCode("pla").build()));
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, formatted);
        assertFalse(errors.isEmpty());
    }
    
    @Test
    public void notSndButNoObservationsShouldFail() {
        var formatted = getDefaultFormatted().build();
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().build());
        formatted.setTotal(0);
        formatted.setInverts(0);
        formatted.setCode("pla");
        formatted.setSpecies(
                Optional.of(ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(1).build())
                        .observableItemName("Pictilabrus laticlavius").letterCode("pla").build()));
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, formatted);
        assertFalse(errors.isEmpty());
    }
    
        
    @Test
    public void measureMethodWithNoObsShouldfail() {
        var formatted = getDefaultFormatted().build();
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().build());
        formatted.setTotal(1);
        formatted.setMethod(1);
        formatted.setCode("PLA");
        formatted.setSpecies(
                Optional.of(ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(1).build())
                        .observableItemName("Pictilabrus laticlavius").letterCode("pla").build()));
        var errors = measurementValidation.validate(null, formatted, false);
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().startsWith("Row contains no measurements")));
    }
}
