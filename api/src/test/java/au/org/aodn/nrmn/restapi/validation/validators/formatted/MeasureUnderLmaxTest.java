package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.validation.validators.row.formatted.MeasureUnderLmax;
import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MeasureUnderLmaxTest extends  FormattedTestProvider {
    @Test
    public void underLmaxFish() {
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
                return 800l;
            }
        };
        val validationRule = new MeasureUnderLmax();

        val formatted_valid = getDefaultFormatted().build();
        formatted_valid.setIsInvertSizing(false);
        formatted_valid.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted_valid.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).put(36,3).build());
        formatted_valid.setMethod(1);
        assertTrue(validationRule.valid(formatted_valid).isValid());

        val formatted_invalid = getDefaultFormatted().build();
        formatted_invalid.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted_invalid.setIsInvertSizing(false);
        formatted_invalid.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).put(37,3).build());
        formatted_invalid.setMethod(1);
        assertTrue(validationRule.valid(formatted_invalid).isInvalid());
    }

    public void underLmaxInverts() {
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
                return true;
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
        val validationRule = new MeasureUnderLmax();

        val formatted_valid = getDefaultFormatted().build();
        formatted_valid.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted_valid.setIsInvertSizing(true);
        formatted_valid.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).put(35,3).build());
        formatted_valid.setMethod(1);
        assertTrue(validationRule.valid(formatted_valid).isValid());

        val formatted_invalid = getDefaultFormatted().build();
        formatted_invalid.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted_invalid.setIsInvertSizing(true);
        formatted_invalid.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).put(36,3).build());
        formatted_invalid.setMethod(1);
        assertTrue(validationRule.valid(formatted_invalid).isInvalid());
    }
}
