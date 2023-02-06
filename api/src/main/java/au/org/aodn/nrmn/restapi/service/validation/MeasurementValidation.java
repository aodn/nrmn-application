package au.org.aodn.nrmn.restapi.service.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.data.model.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.enums.ObservableItemType;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;

@Service
public class MeasurementValidation {

    private static final double[] FISH_VALUES = { 2.5, 5, 7.5, 10, 12.5, 15, 20, 25, 30, 35, 40, 50, 62.5, 75, 87.5,
            100, 112.5, 125, 137.5, 150, 162.5, 175, 187.5, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750,
            800, 850, 900, 950, 1000 };

    private static final double[] INVERT_VALUES = { 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5,
            9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 16, 17, 18, 19, 20, 22, 24, 26, 28, 30 };

    private Boolean validateRowZeroOrOneInvertsTotal(StagedRowFormatted row, Integer observationTotal) {
        if (row.getTotal() == null)
            return observationTotal == 0 || observationTotal == 1;

        if (row.getInverts() == null || !(row.getInverts() == row.getTotal() && row.getTotal() == observationTotal))
            return false;
        return observationTotal == 0 || observationTotal == 1;
    }

    public Collection<ValidationCell> validateMeasurements(ProgramValidation validation, StagedRowFormatted row) {
        var errors = new ArrayList<ValidationCell>();

        if (row.getMeasureJson() == null)
            return errors;

        var observationTotal = row.observationTotal();

        // VALIDATION: Debris Zero observations
        if (row.isDebrisZero() && !validateRowZeroOrOneInvertsTotal(row, observationTotal)) {
            errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                    "Debris has Value/Total/Inverts not 0 or 1", row.getId(), "total"));
        }

        // VALIDATION: Row has no data and but not flagged as 'Survey Not Done' or
        // 'No Species Found'
        if (observationTotal < 1 && !row.isDebrisZero() && !row.isSurveyNotDone() && !(row.getSpecies().isPresent()
                && row.getSpecies().get().getObsItemType() != null
                && row.getSpecies().get().getObsItemType().getObsItemTypeId() == ObservableItemType.NoSpeciesFound)) {

            // VALIDATION: At least one value recorded in any of the size class columns or
            // in the column Inverts
            if (row.getInverts() != null) {
                errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING,
                        "Row has no data and but not flagged as 'Survey Not Done' or 'No Species Found'",
                        row.getId(), "total"));
            } else {
                errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                        "Row has no data and no value recorded for inverts", row.getId(), "inverts"));
            }
        } else if (row.getSpecies().isPresent() && row.getSpecies().get().getObsItemType() != null
                && row.getSpecies().get().getObsItemType().getObsItemTypeId() == ObservableItemType.NoSpeciesFound
                && !validateRowZeroOrOneInvertsTotal(row, observationTotal)) {
            errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING,
                    "'No Species Found' has Value/Total/Inverts not 0 or 1", row.getId(), "total"));
        } else if (row.isSurveyNotDone() && !validateRowZeroOrOneInvertsTotal(row, observationTotal)) {
            errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING,
                    "'Survey Not Done' has Value/Total/Inverts not 0 or 1", row.getId(), "total"));
        }
        // VALIDATION: Abundance CheckSums
        if (errors.size() < 1 && row.getTotal() != null && !row.getTotal().equals(observationTotal))
            errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                    "Calculated total is " + observationTotal, row.getId(), "total"));

        return errors;
    }

    // VALIDATION: Species size within L5 - L95
    private Collection<ValidationCell> validateMeasureRange(Long rowId, String species, Boolean isInvertSized,
            Map<Integer, Integer> measurements, UiSpeciesAttributes speciesAttributes) {

        Collection<ValidationCell> errors = new ArrayList<ValidationCell>();

        double[] range = isInvertSized ? INVERT_VALUES : FISH_VALUES;

        Double l5 = speciesAttributes.getL5() != null ? speciesAttributes.getL5() : 0;
        Double l95 = speciesAttributes.getL95() != null ? speciesAttributes.getL95() : 0;

        if (l5 != 0 && l95 != 0) {
            var outOfRange = measurements.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey() > 0 && ((l5 > 0 && range[entry.getKey() - 1] < l5)
                            || (l95 > 0 && range[entry.getKey() - 1] > l95)))
                    .map(Map.Entry::getKey).collect(Collectors.toList());

            if (!outOfRange.isEmpty()) {
                String message = (isInvertSized ? "Invert measurements" : "Measurements") + " outside L5/95 [" + l5
                        + "," + l95 + "] for [" + species + "]";
                outOfRange.stream().forEach(col -> errors.add(new ValidationCell(ValidationCategory.DATA,
                        ValidationLevel.INFO, message, rowId, "measurements." + col.toString())));
            }
        }
        return errors;
    }

    // VALIDATION: Species size below LMax
    private Collection<ValidationCell> validateMeasureUnderMax(Boolean isExtended, StagedRowFormatted row,
            UiSpeciesAttributes speciesAttributes) {

        var errors = new ArrayList<ValidationCell>();

        var isInvertSized = isExtended && row.getIsInvertSizing();
        var range = isInvertSized ? INVERT_VALUES : FISH_VALUES;

        var lMax = speciesAttributes.getLmax() != null ? speciesAttributes.getLmax() : 0;
        if (lMax != 0) {

            var outOfRange = row.getMeasureJson().entrySet()
                    .stream()
                    .filter(entry -> entry.getKey() > 0 && range[entry.getKey() - 1] > lMax)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!outOfRange.isEmpty()) {
                var message = (isInvertSized ? "Invert measurement" : "Measurement") + " above Lmax [" + lMax
                        + "] for [" + row.getRef().getSpecies() + "]";
                outOfRange.stream().forEach(col -> errors.add(new ValidationCell(ValidationCategory.DATA,
                        ValidationLevel.INFO, message, row.getId(), "measurements." + col.toString())));
            }
        }
        return errors;
    }

    // VALIDATION: Species Abundance Check
    public Collection<ValidationCell> validateAbundance(StagedRowFormatted row,
            UiSpeciesAttributes speciesAttributes) {
        var errors = new ArrayList<ValidationCell>();
        if (Arrays.asList(1, 2).contains(row.getMethod()) && speciesAttributes != null) {
            var maxAbundance = speciesAttributes.getMaxAbundance();
            var observationTotal = row.observationTotal();
            if (maxAbundance != null && maxAbundance < observationTotal) {
                var message = "Abundance exceeds " + maxAbundance + " for species " + row.getRef().getSpecies();
                errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.INFO, message, row.getId(), "total"));
            }
        }
        return errors;
    }

    public Collection<ValidationCell> validate(UiSpeciesAttributes speciesAttributes, StagedRowFormatted row,
            Boolean isExtended) {

        var errors = new ArrayList<ValidationCell>();

        // this replaces the `validateMeasure` predicate
        var isMeasureMethod = !Arrays.asList(3, 4, 5).contains(row.getMethod());
        if (isMeasureMethod) {
            if (row.getMeasureJson().size() > 0) {
                errors.addAll(validateMeasureRange(row.getId(), row.getRef().getSpecies(), row.getIsInvertSizing(),
                        row.getMeasureJson(), speciesAttributes));
                errors.addAll(validateMeasureUnderMax(isExtended, row, speciesAttributes));
                errors.addAll(validateAbundance(row, speciesAttributes));
            } else {
                if(row.getInverts() == null || row.getInverts() < 1)
                    errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                        "Row contains no measurements", row.getId(), "measurements.1"));
            }
        }
        return errors;
    }
}
