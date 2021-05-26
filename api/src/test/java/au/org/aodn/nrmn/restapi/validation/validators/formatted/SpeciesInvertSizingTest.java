package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.validation.validators.row.formatted.SpeciesInvertSizing;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesInvertSizingTest extends  FormattedTestProvider {
    @Test
    public void matchingInvertSizingShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        val specAttribute = new UiSpeciesAttributes(){
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
                return false;
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
                return null;
            }

            @Override
            public Long getLmax() {
                return null;
            }
        };
        formatted.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted.setMethod(1);
        formatted.setIsInvertSizing(Optional.of(false));
        val validationRule = new SpeciesInvertSizing();
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }
}
