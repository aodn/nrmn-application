package au.org.aodn.nrmn.restapi.validation.measurement;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.data.model.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.process.FormattedTestProvider;

import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

@ExtendWith(MockitoExtension.class)
class SpeciesAbundanceCheckTest extends  FormattedTestProvider {

    @InjectMocks
    MeasurementValidation measurementValidation;

    UiSpeciesAttributes specAttribute = new UiSpeciesAttributes(){
        @Override
        public Long getId() {
            return 1L;
        }

        @Override
        public String getSpeciesName() {
            return null;
        }

        @Override
        public String getCommonName() {
            return null;
        }

        @Override
        public Boolean getIsInvertSized() {
            return null;
        }

        @Override
        public Double getL5() {
            return 2.5;
        }

        @Override
        public Double getL95() {
            return 10.12;
        }

        @Override
        public Long getMaxAbundance() {
            return 30L;
        }

        @Override
        public Double getLmax() {
            return 20.0;
        }
    };


    @Test
    public void TotalUnderMaxAbundanceShouldSuccess() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted.setTotal(20);
        formatted.setMethod(1);
        var errors = measurementValidation.validateAbundance(formatted, specAttribute);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void TotalAboveMaxAbundanceShouldFailed() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 1).put(3, 15).put(4, 15).build());
        formatted.setMethod(1);
        var errors = measurementValidation.validateAbundance(formatted, specAttribute);
        final String expectedError = "Abundance exceeds 30";
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().startsWith(expectedError)));
    }

    @Test
    public void outOfScopeShouldSuccess() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(4);
        formatted.setSpeciesAttributesOpt(Optional.empty());
        var errors = measurementValidation.validateAbundance(formatted, specAttribute);
        assertTrue(errors.isEmpty());
    }
}
