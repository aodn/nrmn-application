package au.org.aodn.nrmn.restapi.service.validation;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.StatusJobType;
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

    @Autowired
    SiteValidation siteValidation;

    @Autowired
    StagedJobRepository stagedJobRepository;

    public Collection<SurveyValidationError> checkData(ProgramValidation validation, Boolean isExtended,
            Collection<StagedRowFormatted> rows) {

        var results = new ValidationResultSet();

        /** Row-level Checks */
        for (var row : rows) {

            // Validate measurements if species attributes are present
            var speciesAttrib = row.getSpeciesAttributesOpt();
            if (speciesAttrib.isPresent())
                results.addAll(speciesMeasurement.validate(speciesAttrib.get(), row, isExtended),
                        false);

            // Total Checksum & Missing Data
            results.addAll(speciesMeasurement.validateMeasurements(validation, row), false);

            // Row Method is valid for species
            results.add(surveyValidation.validateSpeciesBelowToMethod(row), false);

            // Validate M3, M4 and M5 rows have zero inverts
            results.add(surveyValidation.validateInvertsZeroOnM3M4M5(row), false);

            // Date is not in the future or too far in the past
            results.add(surveyValidation.validateDateRange(validation, row), false);

            // Validate survey is at site location
            results.add(siteValidation.validateSurveyAtSite(row));
        }

        var res = new HashSet<SurveyValidationError>();
        res.addAll(results.getAll());
        res.remove(null);
        return res;
    }

    public ValidationResponse generateSummary(Collection<StagedRowFormatted> mappedRows) {
        var response = new ValidationResponse();
        response.setRowCount(mappedRows.size());

        var distinctSites = mappedRows.stream().map(r -> r.getRef().getSiteCode().toUpperCase())
                .filter(s -> s.length() > 0).distinct().collect(Collectors.toList());
        var distinctSitesExisting = mappedRows.stream().filter(r -> r.getSite() != null)
                .map(r -> r.getSite().getSiteCode().toUpperCase()).distinct()
                .collect(Collectors.toList());
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
                .map(d -> Normalizer.normalize(d.getRef().getPqs(), Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "")
                        .toUpperCase())
                .filter(d -> d.length() > 0 && !d.equalsIgnoreCase("0")).distinct()
                .collect(Collectors.toList());
        var distinctBuddies = mappedRows.stream().flatMap(r -> Stream.of(r.getRef().getBuddy().split(",")))
                .map(d -> Normalizer.normalize(d, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                        .toUpperCase())
                .filter(d -> d.length() > 0).distinct().collect(Collectors.toList());
        distinctSurveyDivers.addAll(distinctPQDivers);
        distinctSurveyDivers.addAll(distinctBuddies);

        // Map diver full names to initials and then use the distinct count of initials
        // to determine the number of distinct divers
        var distinctDiverInitials = distinctSurveyDivers.stream().map(s -> {
            var diver = divers.stream()
                    .filter(d -> StringUtils.isNotEmpty(d.getFullName())
                            && Normalizer.normalize(d.getFullName(), Normalizer.Form.NFD)
                                    .replaceAll("[^\\p{ASCII}]", "")
                                    .equalsIgnoreCase(s))
                    .findFirst();
            return diver.isPresent() ? diver.get().getInitials() : s;
        }).distinct().collect(Collectors.toList());

        var totalDistinctDivers = distinctDiverInitials.size();

        distinctDiverInitials.removeIf(
                n -> divers.stream().anyMatch(
                        d -> d.getInitials() != null && d.getInitials().equalsIgnoreCase(n)));

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

    private static Logger logger = LoggerFactory.getLogger(ValidationProcess.class);

    public void revalidateIngestedJobs() {
        var sheetErrors = new HashSet<SurveyValidationError>();
        for (var job : stagedJobRepository.findAll().stream().filter(j -> j.getStatus() == StatusJobType.INGESTED).collect(Collectors.toList())) {
            var validation = ProgramValidation.fromProgram(job.getProgram());
            var rows = rowRepository.findRowsByJobId(job.getId());
            var species = speciesFormatting.getSpeciesForRows(rows);
            var mappedRows = speciesFormatting.formatRowsWithSpecies(rows, species);
            var errors = surveyValidation.validateSurveys(validation, job.getIsExtendedSize(), mappedRows);
            for(var e : errors.stream().filter(e -> e.getLevelId() == ValidationLevel.BLOCKING && !e.getMessage().contains("Survey exists")).collect(Collectors.toList())) {
                logger.info("Job " + job.getId() + " " + e.getMessage());
            }
            sheetErrors.addAll(errors);
        }
    }

    public ValidationResponse process(StagedJob job) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var hasAdminRole = authentication != null
                ? authentication.getAuthorities().stream()
                        .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))
                : false;

        var rows = rowRepository.findRowsByJobId(job.getId());

        var validation = ProgramValidation.fromProgram(job.getProgram());

        var enteredSiteCodes = rows.stream().map(s -> s.getSiteCode().toUpperCase())
                .collect(Collectors.toSet());
        var siteCodes = siteRepository.getAllSiteCodesMatching(enteredSiteCodes);
        var sheetErrors = new HashSet<SurveyValidationError>();

        var species = speciesFormatting.getSpeciesForRows(rows);
        sheetErrors
                .addAll(dataValidation.checkFormatting(validation, job.getIsExtendedSize(), true,
                        siteCodes, species, rows));
        var mappedRows = speciesFormatting.formatRowsWithSpecies(rows, species);

        var response = generateSummary(mappedRows);

        if (hasAdminRole) {
            response.setErrors(sheetErrors);
            return response;
        }

        sheetErrors.addAll(checkData(validation, job.getIsExtendedSize(), mappedRows));

        sheetErrors.addAll(surveyValidation.validateSurveys(validation, job.getIsExtendedSize(), mappedRows));

        sheetErrors.addAll(surveyValidation.validateSurveyGroups(validation, false, mappedRows));

        response.setIncompleteSurveyCount(
                sheetErrors.stream().filter(e -> e.getMessage().contains("Survey incomplete")).count());
        response.setExistingSurveyCount(
                sheetErrors.stream().filter(e -> e.getMessage().contains("Survey exists:")).count());

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
