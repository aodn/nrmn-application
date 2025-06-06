package au.org.aodn.nrmn.restapi.service.validation;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.formatting.SpeciesFormattingService;

@Slf4j
@Component
public class ValidationProcess {

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    StagedRowRepository rowRepository;

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


    public Collection<SurveyValidationError> checkData(ProgramValidation validation, Boolean isExtended,
            Collection<StagedRowFormatted> rows) {

        var results = new ValidationResultSet();
        /*
         * Row-level Checks
         * */
        for (var row : rows) {

            // Validate measurements if species attributes are present
            var speciesAttrib = row.getSpeciesAttributesOpt();
            speciesAttrib.ifPresent(uiSpeciesAttributes -> results.addAll(speciesMeasurement.validate(uiSpeciesAttributes, row, isExtended), false));

            // Total Checksum & Missing Data
            results.addAll(speciesMeasurement.validateMeasurements(validation, row), false);

            // Row Method is valid for species
            results.add(surveyValidation.validateSpeciesBelowToMethod(false, row), false);

            // Validate M3, M4 and M5 rows have zero inverts
            results.add(surveyValidation.validateInvertsZeroOnM3M4M5(row), false);

            // Date is not in the future or too far in the past
            results.add(surveyValidation.validateDateRange(validation, row), false);

            // Validate survey is at site location
            if(validation != ProgramValidation.NONE)
                results.add(siteValidation.validateSurveyAtSite(row));
        }

        var res = new HashSet<>(results.getAll());
        res.remove(null);
        return res;
    }

    public ValidationResponse generateSummary(Collection<StagedRowFormatted> mappedRows) {
        var response = new ValidationResponse();
        response.setRowCount(mappedRows.size());

        var distinctSites = mappedRows.stream().map(r -> r.getRef().getSiteCode().toUpperCase())
                .filter(s -> !s.isEmpty()).distinct().collect(Collectors.toList());
        var distinctSitesExisting = mappedRows.stream().filter(r -> r.getSite() != null)
                .map(r -> r.getSite().getSiteCode().toUpperCase()).distinct().collect(Collectors.toList());
        response.setSiteCount(distinctSites.size());

        var foundSites = new HashMap<String, Boolean>();
        distinctSites.forEach(s -> foundSites.put(s, !distinctSitesExisting.contains(s)));
        response.setFoundSites(foundSites);
        response.setNewSiteCount(foundSites.values().stream().filter(e -> e).count());

        // Diver Count

        var divers = diverRepository.getAll();

        var distinctSurveyDivers = mappedRows.stream()
                .map(d -> Normalizer.normalize(d.getRef().getDiver(), Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "").toUpperCase())
                .filter(d -> !d.isEmpty()).distinct().collect(Collectors.toList());

        var distinctPQDivers = mappedRows.stream()
                .map(d -> Normalizer.normalize(d.getRef().getPqs(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                        .toUpperCase())
                .filter(d -> !d.isEmpty() && !d.equalsIgnoreCase("0")).distinct().collect(Collectors.toList());

        var distinctBuddies = mappedRows.stream().flatMap(r -> Stream.of(r.getRef().getBuddy().split(",")))
                .map(d -> Normalizer.normalize(d, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase())
                .filter(d -> !d.isEmpty()).distinct().collect(Collectors.toList());

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
                .filter(r -> !r.isEmpty()).distinct().collect(Collectors.toList());

        var distinctObsItems = obsItemNames.size();
        var distinctObsItemsExisting = mappedRows.stream().filter(r -> r.getSpecies().isPresent())
                .map(r -> r.getSpecies().get().getObservableItemName()).distinct().count();
        var distinctNotPresentObsItem = mappedRows.stream().map(r -> r.getRef().getSpecies().toUpperCase())
                .filter(r -> r.equalsIgnoreCase("SURVEY NOT DONE")).distinct().count();
        response.setNewObsItemCount(distinctObsItems - distinctObsItemsExisting - distinctNotPresentObsItem);

        response.setObsItemCount(distinctObsItems);
        return response;
    }
    /**
     * Validate a staged job, if validation failed, we should not allow job move from staged to ingested
     * @param job - After upload and hence become staged
     * @return validation result
     */
    public ValidationResponse process(StagedJob job) {
        var rows = rowRepository.findRowsByJobId(job.getId());
        var validation = ProgramValidation.fromProgram(job.getProgram());
        var enteredSiteCodes = rows.stream().map(s -> s.getSiteCode().toUpperCase()).collect(Collectors.toSet());
        var siteCodes = siteRepository.getAllSiteCodesMatching(enteredSiteCodes);
        var species = speciesFormatting.getSpeciesForRows(rows);
        var sheetErrors = new HashSet<>(dataValidation.checkFormatting(validation, job.getIsExtendedSize(), true, siteCodes, species, rows));
        var mappedRows = speciesFormatting.formatRowsWithSpecies(rows, species);
        var response = generateSummary(mappedRows);

        sheetErrors.addAll(checkData(validation, job.getIsExtendedSize(), mappedRows));

        sheetErrors.addAll(siteValidation.validateSites(mappedRows));

        sheetErrors.addAll(surveyValidation.validateSurveys(validation, job.getIsExtendedSize(), mappedRows));
        
        sheetErrors.addAll(surveyValidation.validateSurveyGroups(validation, false, mappedRows));

        response.setIncompleteSurveyCount(sheetErrors.stream().filter(e -> e.getMessage().contains("Survey incomplete")).count());
        response.setExistingSurveyCount(sheetErrors.stream().filter(e -> e.getMessage().contains("Survey exists:")).count());

        var distinctSurveys = mappedRows
                .stream()
                .filter(r -> Set.of(1, 2).contains(r.getMethod()))
                .map(StagedRowFormatted::getSurvey)
                .distinct()
                .count();

        response.setSurveyCount(distinctSurveys);

        var errorId = 0;
        for (var validationError : sheetErrors)
            validationError.setId(errorId++);

        response.setErrors(sheetErrors);

        return response;
    }
}
