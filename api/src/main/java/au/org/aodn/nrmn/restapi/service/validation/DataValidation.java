package au.org.aodn.nrmn.restapi.service.validation;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.enums.Directions;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.util.TimeUtils;

import javax.annotation.PreDestroy;

import static au.org.aodn.nrmn.restapi.util.NumberUtils.getDecimalCount;
import static au.org.aodn.nrmn.restapi.util.Constants.COORDINATE_VALID_DECIMAL_COUNT;

@Service
public class DataValidation {

    protected ExecutorService executor = Executors.newFixedThreadPool(
    //        Runtime.getRuntime().availableProcessors()
            1
    );

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    ObservableItemRepository observableItemRepository;

    @Autowired
    ObservationRepository observationRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    StagedRowRepository rowRepository;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    MeasurementValidation speciesMeasurement;

    @PreDestroy
    public void close() {
        executor.shutdown();
    }

    private static final int INVALID_INT = Integer.MIN_VALUE;
    private static final double INVALID_DOUBLE = Double.NEGATIVE_INFINITY;
    private static final Pattern VALID_DEPTH_SURVEY_NUM = Pattern.compile("^[0-9]+(\\.[0-9])?$");

    // VALIDATION: Rows duplicated
    public Collection<SurveyValidationError> checkDuplicateRows(
            boolean includeTotal,
            boolean includeSpeciesCode,
            Collection<StagedRow> rows) {
        var mappedRows = new HashMap<String, List<Long>>();
        var mappedSpecies = new HashMap<String, String>();
        rows.forEach(r -> {
            var rowHash = r.getContentsHash(includeTotal, includeSpeciesCode, false);
            var rowIds = mappedRows.getOrDefault(rowHash, new ArrayList<>());
            rowIds.add(r.getId());
            if (!mappedSpecies.containsKey(rowHash))
                mappedSpecies.put(rowHash, r.getSpecies());
            mappedRows.put(rowHash, rowIds);
        });
        var duplicateRows = new ArrayList<SurveyValidationError>();
        mappedRows.forEach((r, rowIds) -> {
            if (rowIds.size() > 1) {
                duplicateRows.add(
                        new SurveyValidationError(null, ValidationLevel.DUPLICATE, mappedSpecies.get(r), rowIds, null));

            }
        });
        return duplicateRows;
    }

    public Collection<SurveyValidationError> checkFormatting(
            ProgramValidation validation,
            Boolean isExtendedSize,
            Boolean isIngest,
            Collection<String> siteCodes,
            Collection<ObservableItem> species,
            Collection<StagedRow> rows) {

        var diverNames = new ArrayList<String>();
        for (var d : diverRepository.getAll()) {
            diverNames.add(Normalizer.normalize(d.getFullName(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                    .toUpperCase());
            diverNames.add(Normalizer.normalize(d.getInitials(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                    .toUpperCase());
        }

        final var errors = new ValidationResultSet();
        if (rows == null || rows.isEmpty()) {
            errors.add(null, ValidationLevel.BLOCKING, "ID", "Survey data is missing");
            return errors.getAll();
        }

        errors.addAll(checkDuplicateRows(false, true, rows));

        List<Callable<Void>> tasks = new ArrayList<>();
        for (final StagedRow row : rows) {
            Callable<Void> task = () -> {
                var rowId = row.getId();

                // Site
                if ((validation != ProgramValidation.NONE)
                        && (row.getSiteCode() == null || !siteCodes.contains(row.getSiteCode().toLowerCase())))
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

                if (isIngest) {

                    if (StringUtils.isEmpty(row.getBuddy())) {
                        errors.add(rowId, ValidationLevel.WARNING, "buddy", "Diver does not exist");
                    } else if (unknownBuddies.size() == 1) {
                        errors.add(rowId, ValidationLevel.WARNING, "buddy",
                                "Diver " + unknownBuddies.get(0) + " does not exist");
                    } else if (unknownBuddies.size() > 1) {
                        errors.add(rowId, ValidationLevel.WARNING, "buddy",
                                "Divers " + String.join(", ", unknownBuddies) + " do not exist");
                    }

                    // Total
                    if (NumberUtils.toInt(row.getTotal(), INVALID_INT) == INVALID_INT)
                        errors.add(rowId, ValidationLevel.BLOCKING, "total", "Total is not an integer");
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
                if (row.getDirection() != null && StringUtils.isNotEmpty(row.getDirection())
                        && !EnumUtils.isValidEnumIgnoreCase(Directions.class, row.getDirection())
                        && !Arrays.asList("0", "").contains(row.getDirection()))
                    errors.add(rowId, ValidationLevel.BLOCKING, "direction", "Direction is not valid");

                // Latitude
                var latitude = NumberUtils.toDouble(row.getLatitude(), INVALID_DOUBLE);
                if (latitude < -90.0 || 90.0 < latitude || latitude == INVALID_DOUBLE) {
                    errors.add(rowId, ValidationLevel.BLOCKING, "latitude",
                            (latitude == INVALID_DOUBLE) ? "Latitude is not number" : "Latitude is out of bounds");
                } else if (needsToRound(latitude)) {
                    errors.add(rowId, ValidationLevel.WARNING, "latitude", "Latitude will be rounded to 5 decimal places");
                }


                // Longitude
                var longitude = NumberUtils.toDouble(row.getLongitude(), INVALID_DOUBLE);
                if (longitude < -180 || 180 < longitude || longitude == INVALID_DOUBLE) {
                    errors.add(rowId, ValidationLevel.BLOCKING, "longitude",
                            (latitude == INVALID_DOUBLE) ? "Longitude is not number" : "Longitude is out of bounds");
                } else if (needsToRound(longitude)) {
                    errors.add(rowId, ValidationLevel.WARNING, "longitude", "Longitude will be rounded to 5 decimal places");
                }

                // Date
                try {
                    LocalDate.parse(row.getDate(), TimeUtils.getRowDateFormatter());
                } catch (DateTimeParseException e) {
                    errors.add(rowId, ValidationLevel.BLOCKING, "date", "Date format is not valid");
                }

                // Time
                if (row.getTime() != null && !row.getTime().isEmpty() && !TimeUtils.parseTime(row.getTime()).isPresent())
                    errors.add(rowId, ValidationLevel.WARNING, "time", "Time format is not valid");

                // Block
                if (!Arrays.asList(0, 1, 2).contains(NumberUtils.toInt(row.getBlock(), INVALID_INT))) {
                    errors.add(rowId, ValidationLevel.BLOCKING, "block", "Block must be 0, 1 or 2");
                }

                // Vis
                if (!StringUtils.isBlank(row.getVis())) {
                    Double vis = NumberUtils.toDouble(row.getVis(), INVALID_INT);
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

                // Method
                var method = NumberUtils.toInt(row.getMethod(), INVALID_INT);
                if (method == INVALID_INT)
                    errors.add(rowId, ValidationLevel.BLOCKING, "method", "Method is not an integer");

                // MeasureJson
                if (row.getMeasureJson() != null)
                    row.getMeasureJson().forEach((key, value) -> {
                        if (!StringUtils.isBlank(value)
                                && NumberUtils.toInt(value, INVALID_INT) < 0)
                            errors.add(rowId, ValidationLevel.BLOCKING, key.toString(),
                                    "Measurement is not valid");
                    });

                // Depth
                if (StringUtils.isBlank(row.getDepth()) || !VALID_DEPTH_SURVEY_NUM.matcher(row.getDepth()).matches())
                    errors.add(rowId, ValidationLevel.BLOCKING, "depth", "Depth is invalid, expected: depth[.surveyNum]");

                // RLS Method
                if (validation == ProgramValidation.RLS) {
                    if (!Arrays.asList(0, 1, 2, 10, 12).contains(method))
                        errors.add(rowId, ValidationLevel.BLOCKING, "method", "RLS Method must be [0-2], 10 or 12");

                    if (method == 12 && NumberUtils.toInt(row.getBlock()) != 2)
                        errors.add(rowId, ValidationLevel.BLOCKING, "block", "RLS Method 12 must be recorded on block 2");
                }

                // Block 0
                if (row.getBlock().equalsIgnoreCase("0") &&
                        (isIngest || method != 11)
                        && !Arrays.asList(0, 3, 4, 5).contains(method))
                    errors.add(rowId, ValidationLevel.BLOCKING, "block", "Block 0 is invalid for method");

                // ATRC Method
                if (validation == ProgramValidation.ATRC) {
                    if (!Arrays.asList(0, 1, 2, 3, 4, 5, 7, 10).contains(method) && (isIngest && method == 11))
                        errors.add(rowId, ValidationLevel.BLOCKING, "method", "ATRC Method must be [0-5], 7 or 10");

                    if (method == 7 && NumberUtils.toInt(row.getBlock()) != 2)
                        errors.add(rowId, ValidationLevel.BLOCKING, "block", "ATRC Method 7 must be recorded on block 2");
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

                return null;
            };
            tasks.add(task);
        }

        try {
            executor.invokeAll(tasks);
        }
        catch ( InterruptedException e ) {
            errors.add(null, ValidationLevel.BLOCKING, "ID", "Survey processing error, please retry");
            return errors.getAll();
        }

        return errors.getAll();
    }

    private boolean needsToRound(double number) {
        int decimalPlaces = getDecimalCount(number);
        return decimalPlaces > COORDINATE_VALID_DECIMAL_COUNT;
    }
}
