package au.org.aodn.nrmn.restapi.validation.measurement;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.data.model.ObsItemType;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.model.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;

@ExtendWith(MockitoExtension.class)
class MeasurementRangeTest {

    @InjectMocks
    MeasurementValidation measurementValidation;

    final UiSpeciesAttributes speciesAttributes = new UiSpeciesAttributes() {

        @Override
        public Long getId() {
            return 1l;
        }

        @Override
        public String getSpeciesName() {
            return "Pictilabrus laticlavius";
        }

        @Override
        public String getCommonName() {
            return "Pictilabrus laticlavius";
        }

        @Override
        public Boolean getIsInvertSized() {
            return null;
        }

        @Override
        public Double getL5() {
            return 7.5;
        }

        @Override
        public Double getL95() {
            return 15.0;
        }

        @Override
        public Long getMaxAbundance() {
            return 20l;
        }

        @Override
        public Double getLmax() {
            return 20.0;
        }
    };

    @Test
    void belowL5ShouldFail( ) {
        var formatted = new StagedRowFormatted();
        var row = new StagedRow();
        row.setSpecies("Pictilabrus laticlavius");
        formatted.setRef(row);
        formatted.setIsInvertSizing(false);
        formatted.setCode("PLA");
        formatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("Pictilabrus laticlavius").obsItemType(ObsItemType.builder().obsItemTypeId(6).build()).build()));
        formatted.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 1);
            }
        });
        var errors = measurementValidation.validate(speciesAttributes, formatted, false);
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().startsWith("Measurements outside L5/95")));

        formatted.setIsInvertSizing(true);
        var invertErrors = measurementValidation.validate(speciesAttributes, formatted, false);
        assertTrue(invertErrors.stream().anyMatch(e -> e.getMessage().startsWith("Invert measurements outside L5/95")));
    }

    @Test
    void aboveL95ShouldFail( ) {
        var formatted = new StagedRowFormatted();
        var row = new StagedRow();
        row.setSpecies("Pictilabrus laticlavius");
        formatted.setRef(row);
        formatted.setIsInvertSizing(false);
        formatted.setCode("PLA");
        formatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("Pictilabrus laticlavius").obsItemType(ObsItemType.builder().obsItemTypeId(6).build()).build()));
        formatted.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(7, 1);
            }
        });
        var errors = measurementValidation.validate(speciesAttributes, formatted, false);
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().startsWith("Measurements outside L5/95")));

        formatted.setIsInvertSizing(true);
        var invertErrors = measurementValidation.validate(speciesAttributes, formatted, false);
        assertTrue(invertErrors.stream().anyMatch(e -> e.getMessage().startsWith("Invert measurements outside L5/95")));
    }

    @Test
    void aboveLMaxShouldFail( ) {
        var formatted = new StagedRowFormatted();
        var row = new StagedRow();
        row.setSpecies("Pictilabrus laticlavius");
        formatted.setRef(row);
        formatted.setIsInvertSizing(false);
        formatted.setCode("PLA");
        formatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("Pictilabrus laticlavius").obsItemType(ObsItemType.builder().obsItemTypeId(6).build()).build()));
        formatted.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(37, 1);
            }
        });
        var errors = measurementValidation.validate(speciesAttributes, formatted, true);
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().startsWith("Measurement above Lmax")));

        formatted.setIsInvertSizing(true);
        var invertErrors = measurementValidation.validate(speciesAttributes, formatted, true);
        assertTrue(invertErrors.stream().anyMatch(e -> e.getMessage().startsWith("Invert measurement above Lmax")));
    }

    @Test
    void calculatedTotalIsAnError() {
        var stagedRowFormatted = StagedRowFormatted
                .builder()
                .measureJson(new HashMap<>())
                .total(1)
                .code("NAT")        // Some code that will not trigger Debris zero blocking
                .build();

        stagedRowFormatted.getMeasureJson().put(1, 10);
        stagedRowFormatted.getMeasureJson().put(2, 11);

        var errors = measurementValidation.validateMeasurements(ProgramValidation.NONE, stagedRowFormatted);
        assertTrue(errors.stream().filter(f -> f.getLevelId() == ValidationLevel.BLOCKING && f.getMessage().contains("Calculated total is 21")).findAny().isPresent(),
                "BLOCKING error for total mismatch");
    }
}
