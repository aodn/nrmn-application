package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MeasureUnderLmaxTest extends  FormattedTestProvider {
    @Test
    public void underLmaxShouldSuccess() {
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
                return null;
            }

            @Override
            public Long getLmax() {
                return 20L;
            }
        };
        formatted.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).build());
        formatted.setMethod(1);
        val validationRule = new MeasureUnderLmax();
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }
}