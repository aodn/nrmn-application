package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.validation.validators.row.formatted.SpeciesAbundanceCheck;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesAbundanceCheckTest extends  FormattedTestProvider {

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
        public Long getLmax() {
            return 20L;
        }
    };

    @Test
    public void TotalUnderMaxAbundanceShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        formatted.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted.setTotal(20);
        formatted.setMethod(1);
        val validationRule = new SpeciesAbundanceCheck();
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }

    @Test
    public void TotalAboveMaxAbundanceShouldFailed() {
        val formatted = getDefaultFormatted().build();
        formatted.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted.setTotal(31);
        formatted.setMethod(1);
        val validationRule = new SpeciesAbundanceCheck();
        val res = validationRule.valid(formatted);
        assertTrue(res.isInvalid());
    }
    @Test
    public void outOfScopeShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        formatted.setMethod(4);
        formatted.setSpeciesAttributesOpt(Optional.empty());

        val validationRule = new SpeciesAbundanceCheck();
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }



}
