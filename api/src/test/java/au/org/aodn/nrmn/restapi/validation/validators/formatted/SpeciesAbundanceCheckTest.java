package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.validation.process.ValidationProcess;
import lombok.val;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SpeciesAbundanceCheckTest extends  FormattedTestProvider {

    @InjectMocks
    ValidationProcess validationProcess;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

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
        val formatted = getDefaultFormatted().build();
        formatted.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted.setTotal(20);
        formatted.setMethod(1);
        Collection<ValidationError> errors = validationProcess.validateAbundance(formatted, specAttribute);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void TotalAboveMaxAbundanceShouldFailed() {
        val formatted = getDefaultFormatted().build();
        formatted.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted.setTotal(31);
        formatted.setMethod(1);
        Collection<ValidationError> errors = validationProcess.validateAbundance(formatted, specAttribute);
        assertTrue(!errors.isEmpty());
    }
    @Test
    public void outOfScopeShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        formatted.setMethod(4);
        formatted.setSpeciesAttributesOpt(Optional.empty());
        Collection<ValidationError> errors = validationProcess.validateAbundance(formatted, specAttribute);
        assertTrue(errors.isEmpty());
    }



}
