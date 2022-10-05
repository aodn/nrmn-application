package au.org.aodn.nrmn.restapi.validation.measurement;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.data.model.ObsItemType;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.service.validation.DataValidation;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;

@ExtendWith(MockitoExtension.class)
class NoSpeciesFoundMeasurementsTest {

    @Mock
    DiverRepository diverRepository;
    
    @InjectMocks
    DataValidation dataValidation;

    @InjectMocks
    MeasurementValidation measurementValidation;
        
    @Test
    void outOfScopeShouldSuccess( ) {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setStagedJob(job);
        row.setMeasureJson(ImmutableMap.<Integer, String>builder().put(1, "2").build());
        row.setSpecies("Pictilabrus laticlavius");
        ObservableItem item = ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(1).build()).observableItemName("Pictilabrus laticlavius").letterCode("pla").build();
        Collection<SurveyValidationError> errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, Arrays.asList(), Arrays.asList(item), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("species")));
    }

    @Test
    void emptyMeasureShouldSuccess( ) {
        StagedRowFormatted formatted = new StagedRowFormatted();
        formatted.setCode("snf");
        formatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("No Species Found").obsItemType(ObsItemType.builder().obsItemTypeId(6).build()).build()));
        formatted.setTotal(0);
        formatted.setInverts(0);
        formatted.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 0);
                put(3, 0);
            }
        });
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, formatted);
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("'Survey Not Done' has Value/Total/Inverts not 0 or 1")));
    }

    @Test
    void sndWithGtOneInvertsShouldFail( ) {
        StagedRowFormatted formatted = new StagedRowFormatted();
        formatted.setCode("SND");
        formatted.setTotal(0);
        formatted.setInverts(2);
        formatted.setMeasureJson(new HashMap<Integer, Integer>());
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, formatted);
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("'Survey Not Done' has Value/Total/Inverts not 0 or 1")));
    }

    @Test
    void withMeasuresShouldFail( ) {
        StagedRowFormatted formatted = new StagedRowFormatted();
        formatted.setCode("snf");
        formatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("No Species Found").obsItemType(ObsItemType.builder().obsItemTypeId(6).build()).build()));
        formatted.setTotal(0);
        formatted.setInverts(0);
        formatted.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 3);
                put(3, 4);
            }
        });
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, formatted);
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("has Value/Total/Inverts not 0 or 1")));
    }

    @Test
    void emptyMapMeasureShouldSuccess( ) {
        StagedRowFormatted formatted = new StagedRowFormatted();
        formatted.setCode("snf");
        formatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("No Species Found").obsItemType(ObsItemType.builder().obsItemTypeId(6).build()).build()));
        formatted.setTotal(0);
        formatted.setInverts(0);
        formatted.setMeasureJson(Collections.emptyMap());
        var errors = measurementValidation.validateMeasurements(ProgramValidation.RLS, formatted);
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("'Survey Not Done' has Value/Total/Inverts not 0 or 1")));
    }
}
