package au.org.aodn.nrmn.restapi.validation.process;

import static au.org.aodn.nrmn.restapi.util.SpacialUtil.getDistanceLatLongMeters;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.controller.mapping.StagedRowFormattedMapperConfig;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationRow;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.model.db.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.util.TimeUtils;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

@Component
public class ValidationProcess {

    @Autowired
    StagedRowRepository rowRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    ObservationRepository observationRepository;

    @Autowired
    ObservableItemRepository observableItemRepository;

    private static final int INVALID_INT = Integer.MIN_VALUE;
    private static final double INVALID_DOUBLE = Double.NEGATIVE_INFINITY;
    private static final int OBS_ITEM_TYPE_NO_SPECIES_FOUND = 6;
    private static final Integer[] METHODS_TO_CHECK = { 0, 1, 2, 7, 10 };
    private static final Pattern VALID_DEPTH_SURVEY_NUM = Pattern.compile("^[0-9]+(\\.[0-9])?$");
    private static final LocalDate DATE_MIN_RLS = LocalDate.parse("2006-01-01");
    private static final LocalDate DATE_MIN_ATRC = LocalDate.parse("1991-01-01");

    private static final double[] FISH_VALUES = { 2.5, 5, 7.5, 10, 12.5, 15, 20, 25, 30, 35, 40, 50, 62.5, 75, 87.5,
            100, 112.5, 125, 137.5, 150, 162.5, 175, 187.5, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750,
            800, 850, 900, 950, 1000 };
    private static final double[] INVERT_VALUES = { 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5,
            9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 16, 17, 18, 19, 20, 22, 24, 26, 28, 30 };

    // VALIDATION: Rows duplicated
    public Collection<ValidationRow> checkDuplicateRows(boolean includeTotal, boolean includeSpeciesCode,
            Collection<StagedRow> rows) {
        var mappedRows = new HashMap<String, List<Long>>();
        var mappedSpecies = new HashMap<String, String>();
        rows.stream().forEach(r -> {
            String rowHash = r.getContentsHash(includeTotal, includeSpeciesCode);
            List<Long> rowIds = mappedRows.getOrDefault(rowHash, new ArrayList<Long>());
            rowIds.add(r.getId());
            if (!mappedSpecies.containsKey(rowHash))
                mappedSpecies.put(rowHash, r.getSpecies());
            mappedRows.put(rowHash, rowIds);
        });
        var duplicateRows = new ArrayList<ValidationRow>();
        mappedRows.forEach((r, v) -> {
            if (v.size() > 1) {
                List<Long> rowIds = v.stream().collect(Collectors.toList());
                duplicateRows.add(new ValidationRow(r, rowIds, ValidationLevel.DUPLICATE, mappedSpecies.get(r)));
            }
        });
        return duplicateRows;
    }

    public Collection<ValidationError> checkFormatting(ProgramValidation validation, Boolean isExtendedSize,
            Collection<String> siteCodes, Collection<ObservableItem> species, Collection<StagedRow> rows) {

        var diverNames = new ArrayList<String>();
        for (var d : diverRepository.getAll()) {
            diverNames.add(Normalizer.normalize(d.getFullName(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                    .toUpperCase());
            diverNames.add(Normalizer.normalize(d.getInitials(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                    .toUpperCase());
        }

        var errors = new ValidationResultSet();

        errors.addGlobal(checkDuplicateRows(false, true, rows));

        for (var row : rows) {

            var rowId = row.getId();

            // Site
            if (row.getSiteCode() == null || !siteCodes.contains(row.getSiteCode().toLowerCase()))
                errors.add(rowId, ValidationLevel.BLOCKING, "siteCode", "Site Code does not exist");

            // Diver
            if (row.getDiver() == null || !diverNames.contains(Normalizer.normalize(row.getDiver(), Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "").toUpperCase()))
                errors.add(rowId, ValidationLevel.BLOCKING, "diver", "Diver does not exist");

            // Buddies
            var unknownBuddies = new ArrayList<String>();
            if (StringUtils.isNotEmpty(row.getBuddy())) {
                for (String buddyComponent : row.getBuddy().split(",")) {
                    String buddy = buddyComponent.trim();
                    if (!diverNames.contains(Normalizer.normalize(buddy, Normalizer.Form.NFD)
                            .replaceAll("[^\\p{ASCII}]", "").toUpperCase()))
                        unknownBuddies.add(buddy);
                }
            }

            if (StringUtils.isEmpty(row.getBuddy())) {
                errors.add(rowId, ValidationLevel.WARNING, "buddy", "Diver does not exist", 0);
            } else if (unknownBuddies.size() == 1) {
                errors.add(rowId, ValidationLevel.WARNING, "buddy",
                        "Diver " + unknownBuddies.get(0) + " does not exist", 1);
            } else if (unknownBuddies.size() > 1) {
                errors.add(rowId, ValidationLevel.WARNING, "buddy",
                        "Divers " + String.join(", ", unknownBuddies) + " do not exist", unknownBuddies.size());
            }

            if (StringUtils.isBlank(row.getPqs())) {
                errors.add(rowId, ValidationLevel.WARNING, "P-Qs", "P-Qs Diver is blank");
            } else if (!diverNames.contains(Normalizer.normalize(row.getPqs(), Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "").toUpperCase())) {
                errors.add(rowId, ValidationLevel.WARNING, "P-Qs",
                        String.format("Diver \"%s\" does not exist", row.getPqs()));
            }

            // VALIDATION: Species are not superseded
            if (StringUtils.isNotEmpty(row.getSpecies()) && !row.getSpecies().equalsIgnoreCase("survey not done")) {
                Optional<ObservableItem> observableItem = species.stream()
                        .filter(s -> row.getSpecies().equalsIgnoreCase(s.getObservableItemName())).findAny();
                if (observableItem.isPresent()) {
                    String supersededBy = observableItem.get().getSupersededBy();
                    if (StringUtils.isNotEmpty(supersededBy))
                        errors.add(rowId, ValidationLevel.WARNING, "species", "Superseded by " + supersededBy);
                } else {
                    errors.add(rowId, ValidationLevel.BLOCKING, "species", "Species does not exist");
                }
            }

            // Direction
            if (StringUtils.isNotEmpty(row.getDirection())
                    && !EnumUtils.isValidEnumIgnoreCase(Directions.class, row.getDirection())
                    && !Arrays.asList("0", "").contains(row.getDirection()))
                errors.add(rowId, ValidationLevel.BLOCKING, "direction", "Direction is not valid");

            // Latitude
            var latitude = NumberUtils.toDouble(row.getLatitude(), INVALID_DOUBLE);
            if (latitude < -90.0 || 90.0 < latitude || latitude == INVALID_DOUBLE)
                errors.add(rowId, ValidationLevel.BLOCKING, "latitude",
                        (latitude == INVALID_DOUBLE) ? "Latitude is not number" : "Latitude is out of bounds");

            // Longitude
            var longitude = NumberUtils.toDouble(row.getLongitude(), INVALID_DOUBLE);
            if (longitude < -180 || 180 < longitude || longitude == INVALID_DOUBLE)
                errors.add(rowId, ValidationLevel.BLOCKING, "longitude",
                        (latitude == INVALID_DOUBLE) ? "Longitude is not number" : "Longitude is out of bounds");

            // Date
            try {
                LocalDate.parse(row.getDate(), TimeUtils.getRowDateFormatter());
            } catch (DateTimeParseException e) {
                errors.add(rowId, ValidationLevel.BLOCKING, "date", "Date format is not valid");
            }

            // Time
            if (row.getTime().length() > 0 && !TimeUtils.parseTime(row.getTime()).isPresent())
                errors.add(rowId, ValidationLevel.WARNING, "time", "Time format is not valid");

            // Block
            if (!Arrays.asList(0, 1, 2).contains(NumberUtils.toInt(row.getBlock(), INVALID_INT))) {
                errors.add(rowId, ValidationLevel.BLOCKING, "block", "Block must be 0, 1 or 2");
            }

            // Vis
            if (!StringUtils.isBlank(row.getVis())) {
                Double vis = NumberUtils.toDouble(row.getVis(), (double) INVALID_INT);
                if (vis < 0) {
                    errors.add(rowId, ValidationLevel.BLOCKING, "vis",
                            (vis == (double) INVALID_INT) ? "Vis is not a decimal" : "Vis is not positive");
                } else {
                    if (vis.toString().split("\\.")[1].length() > 1)
                        errors.add(rowId, ValidationLevel.BLOCKING, "vis", "Vis is more than one decimal place");
                }
            }

            // Inverts
            var inverts = NumberUtils.toInt(row.getInverts(), INVALID_INT);
            if (inverts == INVALID_INT)
                errors.add(rowId, ValidationLevel.BLOCKING, "inverts", "Inverts is not an integer");

            // Total
            if (NumberUtils.toInt(row.getTotal(), INVALID_INT) == INVALID_INT)
                errors.add(rowId, ValidationLevel.BLOCKING, "total", "Total is not an integer");

            // Method
            if (NumberUtils.toInt(row.getMethod(), INVALID_INT) == INVALID_INT)
                errors.add(rowId, ValidationLevel.BLOCKING, "method", "Method is not an integer");

            // MeasureJson
            if (row.getMeasureJson() != null)
                row.getMeasureJson().entrySet().stream().forEach(measure -> {
                    if (!StringUtils.isBlank(measure.getValue())
                            && NumberUtils.toInt(measure.getValue(), INVALID_INT) < 0)
                        errors.add(rowId, ValidationLevel.BLOCKING, measure.getKey().toString(),
                                "Measurement is not valid");
                });

            // Depth
            if (StringUtils.isBlank(row.getDepth()) || !VALID_DEPTH_SURVEY_NUM.matcher(row.getDepth()).matches())
                errors.add(rowId, ValidationLevel.BLOCKING, "depth", "Depth is invalid, expected: depth[.surveyNum]");

            // RLS Method
            if (validation == ProgramValidation.RLS
                    && !Arrays.asList(0, 1, 2, 10).contains(NumberUtils.toInt(row.getMethod(), INVALID_INT)))
                errors.add(rowId, ValidationLevel.BLOCKING, "method", "RLS Method must be 0, 1, 2 or 10");

            // Block 0
            if (row.getBlock().equalsIgnoreCase("0")
                    && !Arrays.asList(0, 3, 4, 5).contains(NumberUtils.toInt(row.getMethod(), INVALID_INT)))
                errors.add(rowId, ValidationLevel.BLOCKING, "block", "Block 0 is invalid for method");

            // ATRC Method
            if (validation == ProgramValidation.ATRC) {
                if (!Arrays.asList(0, 1, 2, 3, 4, 5, 7, 10).contains(NumberUtils.toInt(row.getMethod(), INVALID_INT)))
                    errors.add(rowId, ValidationLevel.BLOCKING, "method", "ATRC Method must be [0-5], 7 or 10");

                if (NumberUtils.toInt(row.getMethod()) == 7 && NumberUtils.toInt(row.getBlock()) != 2)
                    errors.add(rowId, ValidationLevel.BLOCKING, "method", "ATRC Method 7 must be recorded on block 2");
            }

            // Validation: Use Invert Sizing is blank
            if (isExtendedSize && StringUtils.isBlank(row.getIsInvertSizing())) {
                errors.add(rowId, ValidationLevel.WARNING, "isInvertSizing", "Use Invert Sizing is blank");
            }

            // Validation: Species Invert Sizing
            if (isExtendedSize && !StringUtils.isBlank(row.getIsInvertSizing()) &&
                    !(row.getIsInvertSizing().equalsIgnoreCase("YES")
                            || row.getIsInvertSizing().equalsIgnoreCase("NO")))
                errors.add(rowId, ValidationLevel.BLOCKING, "isInvertSizing",
                        "Use Invert Sizing must be 'Yes' or 'No'");

        }

        return errors.getAll();
    }

    private boolean validateMeasure(Boolean isExtended, StagedRowFormatted row) {
        if (Arrays.asList(3, 4, 5).contains(row.getMethod()) || !row.getSpeciesAttributesOpt().isPresent())
            return false;

        return !row.getMeasureJson().isEmpty();
    }

    // VALIDATION: Species size within L5 - L95
    private Collection<ValidationCell> validateMeasureRange(Boolean isExtended, StagedRowFormatted row,
            UiSpeciesAttributes speciesAttributes) {

        var errors = new ArrayList<ValidationCell>();

        var isInvertSized = isExtended && row.getIsInvertSizing();
        var range = isInvertSized ? INVERT_VALUES : FISH_VALUES;

        var l5 = speciesAttributes.getL5() != null ? speciesAttributes.getL5() : 0;
        var l95 = speciesAttributes.getL95() != null ? speciesAttributes.getL95() : 0;

        if (l5 != 0 && l95 != 0) {
            var outOfRange = row.getMeasureJson().entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() != 0 && (l5 > 0 && range[entry.getKey() - 1] < l5)
                            || (l95 > 0 && range[entry.getKey() - 1] > l95))
                    .map(Map.Entry::getKey).collect(Collectors.toList());

            if (!outOfRange.isEmpty()) {
                var message = (isInvertSized ? "Invert measurements" : "Measurements") + " outside L5/95 [" + l5
                        + "," + l95 + "] for [" + row.getRef().getSpecies() + "]";
                outOfRange.stream().forEach(col -> errors.add(new ValidationCell(ValidationCategory.DATA,
                        ValidationLevel.INFO, message, row.getId(), col.toString())));
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

            List<Integer> outOfRange = row.getMeasureJson().entrySet().stream()
                    .filter(entry -> range[entry.getKey() - 1] > lMax)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!outOfRange.isEmpty()) {
                var message = (isInvertSized ? "Invert measurement " : "Measurement ") + " is above Lmax [" + lMax
                        + "] for Species [" + row.getRef().getSpecies() + "]";
                outOfRange.stream().forEach(col -> errors.add(new ValidationCell(ValidationCategory.DATA,
                        ValidationLevel.INFO, message, row.getId(), col.toString())));
            }
        }
        return errors;
    }

    // VALIDATION: Species Abundance Check
    public Collection<ValidationError> validateAbundance(StagedRowFormatted row,
            UiSpeciesAttributes speciesAttributes) {
        var errors = new ValidationResultSet();
        if (Arrays.asList(1, 2).contains(row.getMethod()) && speciesAttributes != null) {
            var maxAbundance = speciesAttributes.getMaxAbundance();
            if (maxAbundance != null && row.getTotal() != null && maxAbundance < row.getTotal())
                errors.add(row.getId(), ValidationLevel.INFO, "total",
                        "Exceeds max abundance " + maxAbundance + " for species " + row.getRef().getSpecies() + "");
        }
        return errors.getAll();
    }

    private Boolean validateRowZeroOrOneInvertsTotal(StagedRowFormatted row, Integer observationTotal) {
        if (row.getInverts() == null || row.getTotal() == null)
            return false;
        if (!(row.getInverts() == row.getTotal() && row.getTotal() == observationTotal))
            return false;
        return observationTotal == 0 || observationTotal == 1;
    }

    public Collection<ValidationCell> validateMeasurements(ProgramValidation validation, StagedRowFormatted row) {
        var errors = new ArrayList<ValidationCell>();

        if (row.getMeasureJson() == null)
            return errors;

        var observationTotal = row.getMeasureJson().entrySet().stream().map(Map.Entry::getValue).reduce(0,
                Integer::sum) + (row.getInverts() != null ? row.getInverts() : 0);

        // VALIDATION: Debris Zero observations
        if (row.isDebrisZero() && !validateRowZeroOrOneInvertsTotal(row, observationTotal)) {
            errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                    "Debris has Value/Total/Inverts not 0 or 1", row.getId(), "total"));
        }

        // VALIDATION: Record has no data and but not flagged as 'Survey Not Done' or
        // 'No Species Found'
        if (observationTotal < 1 && !row.isDebrisZero() && !row.isSurveyNotDone() && !(row.getSpecies().isPresent()
                && row.getSpecies().get().getObsItemType() != null
                && row.getSpecies().get().getObsItemType().getObsItemTypeId() == OBS_ITEM_TYPE_NO_SPECIES_FOUND)) {

            // VALIDATION: At least one value recorded in any of the size class columns or
            // in the column Inverts
            if (row.getInverts() != null) {
                errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING,
                        "Record has no data and but not flagged as 'Survey Not Done' or 'No Species Found'",
                        row.getId(), "total"));
            } else {
                errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                        "Record has no data and no value recorded for inverts", row.getId(), "inverts"));
            }
        } else if (row.getSpecies().isPresent() && row.getSpecies().get().getObsItemType() != null
                && row.getSpecies().get().getObsItemType().getObsItemTypeId() == OBS_ITEM_TYPE_NO_SPECIES_FOUND
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

    private ValidationCell validateSpeciesBelowToMethod(StagedRowFormatted row) {

        if (row.getSpecies().isPresent() && row.getSpecies().get().getMethods() != null) {
            var methodIds = row.getSpecies().get().getMethods().stream().map(m -> m.getMethodId())
                    .collect(Collectors.toSet());

            if (!methodIds.contains(row.getMethod()))
                return new ValidationCell(
                        ValidationCategory.DATA, ValidationLevel.WARNING, "Method " + row.getMethod()
                                + " invalid for species " + row.getSpecies().get().getObservableItemName(),
                        row.getId(), "method");

        }
        return null;
    }

    private ValidationCell validateInvertsZeroOnM3M4M5(StagedRowFormatted row) {
        return (row.getMethod() != null && row.getInverts() != null && Arrays.asList(3, 4, 5).contains(row.getMethod())
                && row.getInverts() > 0)
                        ? new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                                "Method " + row.getMethod() + " has value for inverts", row.getId(), "inverts")
                        : null;
    }

    private ValidationError validateSurveyIsNew(StagedRowFormatted row) {
        if (row.getDate() != null && Arrays.asList(METHODS_TO_CHECK).contains(row.getMethod())) {

            var surveyDate = Date.from(row.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            var existingSurveys = surveyRepository.findBySiteDepthSurveyNumDate(row.getSite(), row.getDepth(),
                    row.getSurveyNum(), surveyDate);

            if (!existingSurveys.isEmpty()) {
                var existingSurvey = existingSurveys.stream().findFirst().get();
                var message = "Survey exists: " + existingSurvey.getSurveyId() + " includes "
                        + row.getDecimalSurvey();
                return new ValidationError(ValidationCategory.DATA, ValidationLevel.BLOCKING, message,
                        Arrays.asList(row.getId()), Arrays.asList("siteCode"));
            }

        }
        return null;
    }

    // VALIDATION: Survey coordinates match site coordinates
    private Collection<ValidationCell> validateSurveyAtSite(StagedRowFormatted row) {
        var errors = new ArrayList<ValidationCell>();

        if (row.getSite() == null || row.getSite().getLatitude() == null || row.getSite().getLongitude() == null
                || row.getLatitude() == null || row.getLongitude() == null)
            return errors;

        var distMeters = getDistanceLatLongMeters(row.getSite().getLatitude(), row.getSite().getLongitude(),
                row.getLatitude(), row.getLongitude());

        // Warn if survey is more than 10 meters away from site
        if (distMeters > 10) {
            String message = "Survey coordinates more than 10m from site (" + String.format("%.1f", distMeters) + "m)";
            errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING, message, row.getId(),
                    "latitude"));
            errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING, message, row.getId(),
                    "longitude"));
        }

        return errors;
    }

    private ValidationCell validateDateRange(LocalDate earliest, StagedRowFormatted row) {

        if (row.getDate() == null)
            return null;

        // Validation: Surveys Too Old
        if (row.getDate().isAfter(LocalDate.from(ZonedDateTime.now())))
            return new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING, "Date is in the future",
                    row.getId(), "date");

        // Validation: Future Survey Rule
        if (row.getDate().isBefore(earliest))
            return new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING,
                    "Date must be after " + earliest.toString(), row.getId(), "date");

        return null;
    }

    public ValidationError validateMethod3Quadrats(String transect, List<StagedRowFormatted> rows) {

        var columnNames = new HashSet<String>();
        var rowIds = new HashSet<Long>();

        for (int measureIndex : Arrays.asList(1, 2, 3, 4, 5))
            if (rows.stream().mapToInt(row -> row.getMeasureJson().getOrDefault(measureIndex, 0)).sum() == 0) {
                rowIds.addAll(rows.stream().map(r -> r.getId()).collect(Collectors.toList()));
                columnNames.add(Integer.toString(measureIndex));
            }

        return rowIds.size() > 0 ? new ValidationError(ValidationCategory.SPAN, ValidationLevel.BLOCKING,
                "Missing quadrats in transect " + transect, rowIds, columnNames) : null;
    }

    public Collection<ValidationCell> validateMethod3QuadratsLT50(List<StagedRowFormatted> rows) {
        var errors = new ArrayList<ValidationCell>();

        for (int measureIndex : Arrays.asList(1, 2, 3, 4, 5))
            for (var row : rows) {
                if (row.getMeasureJson().getOrDefault(measureIndex, 0) > 50)
                    errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                            "M3 quadrat more than 50", row.getId(), Integer.toString(measureIndex)));
            }

        return errors;
    }

    public ValidationError validateMethod3QuadratsGT50(String transect, List<StagedRowFormatted> rows) {

        var columnNames = new HashSet<String>();

        for (var measureIndex : Arrays.asList(1, 2, 3, 4, 5)) {
            if (rows.stream().mapToInt(row -> row.getMeasureJson().getOrDefault(measureIndex, 0)).sum() < 50)
                columnNames.add(Integer.toString(measureIndex));
        }

        var rowIds = rows.stream().map(r -> r.getId()).collect(Collectors.toList());
        return columnNames.size() > 0 ? new ValidationError(ValidationCategory.SPAN, ValidationLevel.BLOCKING,
                "Quadrats do not sum to at least 50 in transect " + transect, rowIds, columnNames) : null;
    }

    public ValidationError validateSurveyTransectNumber(List<StagedRowFormatted> surveyRows) {
        var invalidTransectRows = surveyRows.stream()
                .filter(r -> !Arrays.asList(1, 2, 3, 4).contains(r.getSurveyNum())).collect(Collectors.toList());
        if (invalidTransectRows.size() > 0)
            return new ValidationError(ValidationCategory.SPAN, ValidationLevel.WARNING,
                    "Survey group transect invalid",
                    invalidTransectRows.stream().map(r -> r.getId()).collect(Collectors.toList()),
                    Arrays.asList("depth"));
        return null;
    }

    private ValidationError validateSurveyComplete(ProgramValidation validation, List<StagedRowFormatted> surveyRows) {

        if (surveyRows.stream().anyMatch(r -> r.getMethod() == null || r.getBlock() == null))
            return null;

        var messagePrefix = "Survey incomplete: " + surveyRows.get(0).getDecimalSurvey();

        var surveyByMethod = surveyRows.stream().filter(sr -> sr.getMethod() != null && sr.getBlock() != null)
                .collect(Collectors.groupingBy(StagedRowFormatted::getMethod));

        var rowIds = new HashSet<Long>();
        var flagColumns = new HashSet<String>();
        var messages = new ArrayList<String>();

        // VALIDATE: If method = 0 then Block should be 0, 1 or 2
        var method0Rows = surveyByMethod.get(0);
        if (method0Rows != null && method0Rows.stream().anyMatch(r -> !Arrays.asList(0, 1, 2).contains(r.getBlock())))
            return new ValidationError(ValidationCategory.SPAN, ValidationLevel.WARNING,
                    "Method 0 must have block 0, 1 or 2",
                    method0Rows.stream().map(r -> r.getId()).collect(Collectors.toList()), Arrays.asList("block"));

        // VALIDATE: Both M1, M2 present and if ATRC has M3 and at least one method of
        // 3,4,5,7
        var requiredMethods = validation == ProgramValidation.ATRC ? Arrays.asList(1, 2, 3) : Arrays.asList(1, 2);
        var missingMethods = new ArrayList<Integer>(requiredMethods);
        missingMethods.removeAll(surveyByMethod.keySet());
        if (missingMethods.size() > 0) {
            var missingMethodsList = missingMethods.stream().map(m -> m.toString()).collect(Collectors.toList());
            messages.add("missing M" + String.join(", M", missingMethodsList));
            rowIds.addAll(surveyRows.stream().map(r -> r.getId()).collect(Collectors.toList()));
            flagColumns.add("method");
        }

        // VALIDATE: M1, M2 each has B1, B2 and if ATRC M3 has B0
        var methodsRequired = validation == ProgramValidation.RLS ? Arrays.asList(1, 2) : Arrays.asList(1, 2, 3);
        var level = ValidationLevel.WARNING;
        for (var method : methodsRequired) {
            var methodRows = surveyByMethod.get(method);
            if (methodRows == null)
                continue;

            var blocksRequired = validation == ProgramValidation.RLS ? new ArrayList<Integer>(Arrays.asList(1, 2))
                    : new ArrayList<Integer>(method == 3 ? Arrays.asList(0) : Arrays.asList(1, 2));

            var hasBlocks = methodRows.stream().map(r -> r.getBlock()).distinct().collect(Collectors.toList());
            var missingBlocks = blocksRequired.stream().filter(b -> !hasBlocks.contains(b))
                    .collect(Collectors.toList());

            if (missingBlocks.size() > 0) {
                if (method == 3) {
                    level = ValidationLevel.BLOCKING;
                    messages.add("M3 " + (hasBlocks.size() > 0 ? "recorded on wrong block" : "missing B0"));
                } else {
                    messages.add("M" + method + " missing B" + String.join(", ",
                            missingBlocks.stream().map(m -> m.toString()).collect(Collectors.toList())));
                }
                rowIds.addAll(methodRows.stream().map(r -> r.getId()).collect(Collectors.toList()));
                flagColumns.add("block");
            }
        }

        if (messages.size() > 0) {
            return new ValidationError(ValidationCategory.SPAN, level,
                    messagePrefix + " " + String.join(". ", messages), rowIds, flagColumns);
        }

        return null;
    }

    public ValidationError validateSurveyGroup(List<StagedRowFormatted> surveyRows) {
        var surveyGroup = surveyRows.stream().collect(Collectors.groupingBy(StagedRowFormatted::getSurveyNum));
        if (!surveyGroup.keySet().containsAll(Arrays.asList(1, 2, 3, 4))) {
            var missingSurveys = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
            missingSurveys.removeAll(surveyGroup.keySet());
            var missingSurveysMessage = missingSurveys.stream().map(s -> s.toString()).collect(Collectors.toList());
            var row = surveyRows.get(0).getRef();
            var message = "Survey group " + row.getSurveyGroup() + " missing transect "
                    + String.join(", ", missingSurveysMessage);
            return new ValidationError(ValidationCategory.SPAN, ValidationLevel.WARNING, message,
                    surveyRows.stream().map(r -> r.getId()).collect(Collectors.toList()), Arrays.asList("depth"));
        }
        return null;
    }

    private Collection<ValidationError> checkSurveys(ProgramValidation validation, Boolean isExtended,
            Map<String, List<StagedRowFormatted>> surveyMap) {
        var res = new HashSet<ValidationError>();

        for (var survey : surveyMap.entrySet()) {
            var surveyRows = survey.getValue();

            if (validation == ProgramValidation.ATRC) {
                // VALIDATE: Survey group transect number valid
                res.add(validateSurveyTransectNumber(surveyRows));
            }

            // VALIDATE: Survey Complete
            res.add(validateSurveyComplete(validation, surveyRows));

            // VALIDATE: Is Existing Survey
            res.add(validateSurveyIsNew(surveyRows.get(0)));
        }

        res.remove(null);

        return res;
    }

    private Collection<ValidationError> checkSurveyGroups(ProgramValidation validation, Boolean isExtended,
            Map<String, List<StagedRowFormatted>> surveyGroupMap) {
        var res = new HashSet<ValidationError>();

        for (var survey : surveyGroupMap.entrySet()) {
            var surveyRows = survey.getValue();

            if (validation == ProgramValidation.ATRC) {
                // VALIDATE: Survey Group Complete
                res.add(validateSurveyGroup(surveyRows));
            }
        }

        res.remove(null);

        return res;
    }

    private Collection<ValidationError> checkMethod3Transects(Boolean isExtended,
            Map<String, List<StagedRowFormatted>> method3SurveyMap) {
        var res = new HashSet<ValidationError>();
        var results = new ValidationResultSet();

        // Validate M3 transects
        for (String transectName : method3SurveyMap.keySet()) {
            res.add(validateMethod3Quadrats(transectName, method3SurveyMap.get(transectName)));
            res.add(validateMethod3QuadratsGT50(transectName, method3SurveyMap.get(transectName)));
            results.addAll(validateMethod3QuadratsLT50(method3SurveyMap.get(transectName)), false);
        }

        res.addAll(results.getAll());
        res.remove(null);

        return res;
    }

    public Collection<ValidationError> checkData(ProgramValidation validation, Boolean isExtended,
            Collection<StagedRowFormatted> rows) {

        var res = new HashSet<ValidationError>();

        var results = new ValidationResultSet();

        /** Row-level Checks */
        for (var row : rows) {

            // FUTURE: move these checks to the `MeasurementValidationService`
            if (row.getSpeciesAttributesOpt().isPresent()) {
                var speciesAttributes = row.getSpeciesAttributesOpt().get();

                // Measure l5, l95 and lMax
                if (validateMeasure(isExtended, row)) {
                    results.addAll(validateMeasureRange(isExtended, row, speciesAttributes), false);
                    results.addAll(validateMeasureUnderMax(isExtended, row, speciesAttributes), false);
                }

                // Abundance check
                res.addAll(validateAbundance(row, speciesAttributes));
            }

            // Total Checksum & Missing Data
            results.addAll(validateMeasurements(validation, row), false);

            // Row Method is valid for species
            results.add(validateSpeciesBelowToMethod(row), false);

            // Validate survey is at site location
            results.addAll(validateSurveyAtSite(row), false);

            // Validate M3, M4 and M5 rows have zero inverts
            results.add(validateInvertsZeroOnM3M4M5(row), false);

            // Date is not in the future or too far in the past
            results.add(validateDateRange(validation == ProgramValidation.RLS ? DATE_MIN_RLS : DATE_MIN_ATRC, row),
                    false);
        }

        res.addAll(results.getAll());
        res.remove(null);

        return res;
    }

    public Collection<ObservableItem> getSpeciesForRows(Collection<StagedRow> rows) {
        var enteredSpeciesNames = rows.stream().map(s -> s.getSpecies()).collect(Collectors.toSet());
        return observableItemRepository.getAllSpeciesNamesMatching(enteredSpeciesNames);
    }

    public Collection<StagedRowFormatted> formatRowsWithSpecies(Collection<StagedRow> rows,
            Collection<ObservableItem> species) {
        var rowMap = rows.stream().collect(Collectors.toMap(StagedRow::getId, r -> r));
        var speciesIds = species.stream().mapToInt(ObservableItem::getObservableItemId).toArray();
        var speciesAttributesMap = observationRepository.getSpeciesAttributesByIds(speciesIds).stream()
                .collect(Collectors.toMap(UiSpeciesAttributes::getSpeciesName, a -> a));
        var speciesMap = species.stream().collect(Collectors.toMap(ObservableItem::getObservableItemName, o -> o));
        var divers = diverRepository.getAll().stream().collect(Collectors.toList());
        var sites = siteRepository.getAll().stream().collect(Collectors.toList());

        var mapperConfig = new StagedRowFormattedMapperConfig();
        var mapper = mapperConfig.getModelMapper(speciesMap, rowMap, speciesAttributesMap, divers, sites);
        return rows.stream().map(stagedRow -> mapper.map(stagedRow, StagedRowFormatted.class))
                .collect(Collectors.toList());
    }

    public ValidationResponse generateSummary(Collection<StagedRowFormatted> mappedRows) {
        var response = new ValidationResponse();
        response.setRowCount(mappedRows.size());

        var distinctSites = mappedRows.stream().map(r -> r.getRef().getSiteCode().toUpperCase())
                .filter(s -> s.length() > 0).distinct().collect(Collectors.toList());
        var distinctSitesExisting = mappedRows.stream().filter(r -> r.getSite() != null)
                .map(r -> r.getSite().getSiteCode().toUpperCase()).distinct().collect(Collectors.toList());
        response.setSiteCount(distinctSites.size());

        var foundSites = new HashMap<String, Boolean>();
        distinctSites.stream().forEach(s -> foundSites.put(s, !distinctSitesExisting.contains(s)));
        response.setFoundSites(foundSites);
        response.setNewSiteCount(foundSites.values().stream().filter(e -> e == true).count());

        // Diver Count

        var divers = diverRepository.getAll();

        var distinctSurveyDivers = mappedRows.stream()
                .map(d -> Normalizer.normalize(d.getRef().getDiver(), Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "").toUpperCase())
                .filter(d -> d.length() > 0).distinct().collect(Collectors.toList());
        var distinctPQDivers = mappedRows.stream()
                .map(d -> Normalizer.normalize(d.getRef().getPqs(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                        .toUpperCase())
                .filter(d -> d.length() > 0 && !d.equalsIgnoreCase("0")).distinct().collect(Collectors.toList());
        var distinctBuddies = mappedRows.stream().flatMap(r -> Stream.of(r.getRef().getBuddy().split(",")))
                .map(d -> Normalizer.normalize(d, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase())
                .filter(d -> d.length() > 0).distinct().collect(Collectors.toList());
        distinctSurveyDivers.addAll(distinctPQDivers);
        distinctSurveyDivers.addAll(distinctBuddies);

        // Map diver full names to initials and then use the distinct count of initials
        // to determine the number of distinct divers
        var distinctDiverInitials = distinctSurveyDivers.stream().map(s -> {
            var diver = divers.stream()
                    .filter(d -> StringUtils.isNotEmpty(d.getFullName())
                            && Normalizer.normalize(d.getFullName(), Normalizer.Form.NFD)
                                    .replaceAll("[^\\p{ASCII}]", "").equalsIgnoreCase(s))
                    .findFirst();
            return diver.isPresent() ? diver.get().getInitials() : s;
        }).distinct().collect(Collectors.toList());

        var totalDistinctDivers = distinctDiverInitials.size();

        distinctDiverInitials.removeIf(
                n -> divers.stream().anyMatch(d -> d.getInitials() != null && d.getInitials().equalsIgnoreCase(n)));

        var totalNewDivers = distinctDiverInitials.size();

        response.setDiverCount(totalDistinctDivers);
        response.setNewDiverCount(totalNewDivers);

        // End Diver Count

        var obsItemNames = mappedRows.stream().map(r -> r.getRef().getSpecies().toUpperCase())
                .filter(r -> r.length() > 0).distinct().collect(Collectors.toList());
        var distinctObsItems = obsItemNames.size();
        var distinctObsItemsExisting = mappedRows.stream().filter(r -> r.getSpecies().isPresent())
                .map(r -> r.getSpecies().get().getObservableItemName()).distinct().count();
        var distinctNotPresentObsItem = mappedRows.stream().map(r -> r.getRef().getSpecies().toUpperCase())
                .filter(r -> r.equalsIgnoreCase("SURVEY NOT DONE")).distinct().count();
        response.setNewObsItemCount(distinctObsItems - distinctObsItemsExisting - distinctNotPresentObsItem);

        response.setObsItemCount(distinctObsItems);
        return response;
    }

    public ValidationResponse process(StagedJob job) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var hasAdminRole = authentication != null
                ? authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))
                : false;

        var rows = rowRepository.findRowsByJobId(job.getId());
        var species = getSpeciesForRows(rows);

        var programName = job.getProgram().getProgramName();
        var validation = Arrays.asList("ATRC", "FRDC").contains(programName.toUpperCase()) ? ProgramValidation.ATRC
                : ProgramValidation.RLS;
        var enteredSiteCodes = rows.stream().map(s -> s.getSiteCode().toUpperCase()).collect(Collectors.toSet());
        var siteCodes = siteRepository.getAllSiteCodesMatching(enteredSiteCodes);
        var sheetErrors = new HashSet<ValidationError>();

        sheetErrors.addAll(checkFormatting(validation, job.getIsExtendedSize(), siteCodes, species, rows));

        var mappedRows = formatRowsWithSpecies(rows, species);

        var response = generateSummary(mappedRows);

        if (hasAdminRole) {
            response.setErrors(sheetErrors);
            return response;
        }

        sheetErrors.addAll(checkData(validation, job.getIsExtendedSize(), mappedRows));

        var surveyMap = mappedRows.stream().collect(Collectors.groupingBy(StagedRowFormatted::getSurvey));
        sheetErrors.addAll(checkSurveys(validation, job.getIsExtendedSize(), surveyMap));
        response.setIncompleteSurveyCount(
                sheetErrors.stream().filter(e -> e.getMessage().contains("Survey incomplete")).count());
        response.setExistingSurveyCount(
                sheetErrors.stream().filter(e -> e.getMessage().contains("Survey exists:")).count());

        var surveyGroupMap = mappedRows.stream().collect(Collectors.groupingBy(StagedRowFormatted::getSurveyGroup));
        sheetErrors.addAll(checkSurveyGroups(validation, job.getIsExtendedSize(), surveyGroupMap));

        var method3SurveyMap = mappedRows.stream()
                .filter(row -> row.getMethod() != null && row.getMethod().equals(3)
                        && !row.getRef().getSpecies().equalsIgnoreCase("Survey Not Done"))
                .collect(Collectors.groupingBy(StagedRowFormatted::getSurvey));
        sheetErrors.addAll(checkMethod3Transects(job.getIsExtendedSize(), method3SurveyMap));

        var distinctSurveys = mappedRows.stream().filter(r -> Arrays.asList(1, 2).contains(r.getMethod()))
                .map(r -> r.getSurvey()).distinct().count();
        response.setSurveyCount(distinctSurveys);

        var errorId = 0;
        for (var validationError : sheetErrors)
            validationError.setId(errorId++);

        response.setErrors(sheetErrors);

        return response;
    }
}
