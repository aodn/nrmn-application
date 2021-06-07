package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.model.db.StagedRow.StagedRowBuilder;
import au.org.aodn.nrmn.restapi.validation.validators.row.formatted.MeasureBetweenL5l95;
import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MeasureBetweenL5l95Test extends FormattedTestProvider{


    @Test
    public void outOfScopeShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        val validationRule = new MeasureBetweenL5l95();
        formatted.setMethod(3);
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }
    @Test
    public void OutOfRangeShouldFail() {
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
                return null;
            }
        };
        formatted.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).build());
        formatted.setMethod(1);
        val validationRule = new MeasureBetweenL5l95();
        val res = validationRule.valid(formatted);
        assertTrue(res.isInvalid());
    }

    @Test
    public void validIfL5NullL95NotNull() {
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
                return null;
            }

            @Override
            public Double getL95() {
                return 100.0;
            }

            @Override
            public Long getMaxAbundance() {
                return null;
            }

            @Override
            public Long getLmax() {
                return 0l;
            }
        };

        val validationRuleL5L95 = new MeasureBetweenL5l95();

        val formatted_valid = getDefaultFormatted().build();
        formatted_valid.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted_valid.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).build());
        formatted_valid.setMethod(1);
        assertTrue(validationRuleL5L95.valid(formatted_valid).isValid());
    }

    @Test
    public void validIfL5NotNullL95Null() {
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
                return null;
            }

            @Override
            public Double getL95() {
                return 10.0;
            }

            @Override
            public Long getMaxAbundance() {
                return null;
            }

            @Override
            public Long getLmax() {
                return 0l;
            }
        };

        val validationRuleL5L95 = new MeasureBetweenL5l95();

        val formatted_valid = getDefaultFormatted().build();
        formatted_valid.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted_valid.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).build());
        formatted_valid.setMethod(1);
        assertTrue(validationRuleL5L95.valid(formatted_valid).isValid());
    }

    @Test
    public void validIfNull() {
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
                return null;
            }

            @Override
            public Double getL95() {
                return null;
            }

            @Override
            public Long getMaxAbundance() {
                return null;
            }

            @Override
            public Long getLmax() {
                return 0l;
            }
        };

        val validationRuleL5L95 = new MeasureBetweenL5l95();

        val formatted_valid = getDefaultFormatted().build();
        formatted_valid.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted_valid.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).put(35,3).build());
        formatted_valid.setMethod(1);
        assertTrue(validationRuleL5L95.valid(formatted_valid).isValid());
    }

    
    @Test
    public void ignoreUseInvertIfNotExtended() {
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
                return 1.25;
            }

            @Override
            public Double getL95() {
                return 2.0;
            }

            @Override
            public Long getMaxAbundance() {
                return null;
            }

            @Override
            public Long getLmax() {
                return 2l;
            }
        };

        val validationRuleL5L95 = new MeasureBetweenL5l95();

        val formatted_valid = getDefaultFormatted().build();
        formatted_valid.setSpeciesAttributesOpt(Optional.of(specAttribute));
        formatted_valid.setRef(StagedRow.builder().stagedJob(StagedJob.builder().isExtendedSize(false).build()).build());
        formatted_valid.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).build());
        formatted_valid.setMethod(1);
        assertTrue(validationRuleL5L95.valid(formatted_valid).isInvalid());
    }
}


