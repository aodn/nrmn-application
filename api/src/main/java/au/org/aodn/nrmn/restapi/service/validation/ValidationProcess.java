package au.org.aodn.nrmn.restapi.service.validation;

import static au.org.aodn.nrmn.restapi.util.SpacialUtil.getDistanceLatLongMeters;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.service.formatting.SpeciesFormattingService;

@Component
public class ValidationProcess {

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
    DataValidation dataValidation;

    @Autowired
    MeasurementValidation speciesMeasurement;

    @Autowired
    SpeciesFormattingService speciesFormatting;

    @Autowired
    SurveyValidation surveyValidation;

    private static final LocalDate DATE_MIN_RLS = LocalDate.parse("2006-01-01");
    private static final LocalDate DATE_MIN_ATRC = LocalDate.parse("1991-01-01");

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

    private ValidationCell validateInvertsZeroOnM3M4M5(StagedRowFormatted row) {
        return (row.getMethod() != null && row.getInverts() != null && Arrays.asList(3, 4, 5).contains(row.getMethod())
                && row.getInverts() > 0)
                        ? new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                                "Method " + row.getMethod() + " has value for inverts", row.getId(), "inverts")
                        : null;
    }

    public Collection<SurveyValidationError> checkData(ProgramValidation validation, Boolean isExtended,
            Collection<StagedRowFormatted> rows) {

        var results = new ValidationResultSet();

        var dateValidation = validation == ProgramValidation.RLS ? DATE_MIN_RLS : DATE_MIN_ATRC;

        /** Row-level Checks */
        for (var row : rows) {

            // Validate measurements if species attributes are present
            var speciesAttrib = row.getSpeciesAttributesOpt();
            if (speciesAttrib.isPresent())
                results.addAll(speciesMeasurement.validate(speciesAttrib.get(), row, isExtended), false);

            // Total Checksum & Missing Data
            results.addAll(speciesMeasurement.validateMeasurements(validation, row), false);

            // Row Method is valid for species
            results.add(validateSpeciesBelowToMethod(row), false);

            // Validate survey is at site location
            results.addAll(validateSurveyAtSite(row), false);

            // Validate M3, M4 and M5 rows have zero inverts
            results.add(validateInvertsZeroOnM3M4M5(row), false);

            // Date is not in the future or too far in the past
            results.add(validateDateRange(dateValidation, row), false);
        }

        var res = new HashSet<SurveyValidationError>();
        res.addAll(results.getAll());
        res.remove(null);
        return res;
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

        var validation = ProgramValidation.fromProgram(job.getProgram());

        var enteredSiteCodes = rows.stream().map(s -> s.getSiteCode().toUpperCase()).collect(Collectors.toSet());
        var siteCodes = siteRepository.getAllSiteCodesMatching(enteredSiteCodes);
        var sheetErrors = new HashSet<SurveyValidationError>();

        var species = speciesFormatting.getSpeciesForRows(rows);
        sheetErrors
                .addAll(dataValidation.checkFormatting(validation, job.getIsExtendedSize(), siteCodes, species, rows));
        var mappedRows = speciesFormatting.formatRowsWithSpecies(rows, species);

        var response = generateSummary(mappedRows);

        if (hasAdminRole) {
            response.setErrors(sheetErrors);
            return response;
        }

        sheetErrors.addAll(checkData(validation, job.getIsExtendedSize(), mappedRows));

        sheetErrors.addAll(surveyValidation.validateSurveys(validation, job.getIsExtendedSize(), mappedRows));

        response.setIncompleteSurveyCount(sheetErrors.stream().filter(e -> e.getMessage().contains("Survey incomplete")).count());
        response.setExistingSurveyCount(sheetErrors.stream().filter(e -> e.getMessage().contains("Survey exists:")).count());

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
