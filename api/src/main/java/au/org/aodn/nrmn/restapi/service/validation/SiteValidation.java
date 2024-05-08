package au.org.aodn.nrmn.restapi.service.validation;

import static au.org.aodn.nrmn.restapi.util.SpacialUtil.getDistanceLatLongMeters;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.data.repository.MeowRegionsRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;

@Service
public class SiteValidation {

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    MeowRegionsRepository meowRegionsRepository;

    private static final Collection<Integer> MEOW_METHODS_TO_CHECK = Arrays.asList(0, 1, 2, 3, 7, 8, 9, 10);

    private SurveyValidationError validateSpeciesEcoregion(Integer siteId, Collection<StagedRowFormatted> survey) {
        var siteEcoregion = siteRepository.getEcoregion(siteId);
        var surveySpecies = survey.stream()
                .filter(r -> MEOW_METHODS_TO_CHECK.contains(r.getMethod()) && r.getSpecies().isPresent()
                        && !r.isSurveyNotDone())

                .collect(Collectors.toList());

        var distinctSurveySpecies = surveySpecies.stream().map(r -> r.getSpecies().get().getObservableItemName())
                .distinct().collect(Collectors.toList());

        distinctSurveySpecies
                .removeAll(meowRegionsRepository.getEcoregionContains(siteEcoregion, distinctSurveySpecies));

        if (distinctSurveySpecies.size() > 0) {
            var message = "Species never observed in " + siteEcoregion;
            return new SurveyValidationError(ValidationCategory.DATA, ValidationLevel.WARNING, message,
                    surveySpecies.stream()
                            .filter(s -> distinctSurveySpecies.contains(s.getSpecies().get().getObservableItemName()))
                            .map(r -> r.getId()).collect(Collectors.toList()),
                    Arrays.asList("species"));
        }

        return null;
    }

    private Collection<SurveyValidationError> checkSites(Map<Integer, List<StagedRowFormatted>> siteMap) {

        var res = new ValidationResultSet();

        // VALIDATION: MEOW ecoregion
        for (var siteRows : siteMap.entrySet())
            res.add(validateSpeciesEcoregion(siteRows.getKey(), siteRows.getValue()));

        return res.getAll();
    }

    public Collection<SurveyValidationError> validateSites(Collection<StagedRowFormatted> mappedRows) {
        var sheetErrors = new HashSet<SurveyValidationError>();

        var siteMap = mappedRows.stream().filter(r -> Objects.nonNull(r.getSite()))
                .collect(Collectors.groupingBy(r -> r.getSite().getSiteId()));

        sheetErrors.addAll(checkSites(siteMap));

        return sheetErrors;
    }

    // VALIDATION: Survey coordinates match site coordinates
    public SurveyValidationError validateSurveyAtSite(StagedRowFormatted row) {

        var site = row.getSite();
        if (site != null) {
            var distMeters = getDistanceLatLongMeters(row.getSite().getLatitude(), row.getSite().getLongitude(),
                    row.getLatitude(), row.getLongitude());

            // Warn if survey is more than 10 meters away from site
            if (distMeters > 10) {
                var message = "Survey coordinates more than 10m from site (" + String.format("%.1f", distMeters) + "m)";
                return new SurveyValidationError(ValidationCategory.DATA, ValidationLevel.WARNING, message,
                        Arrays.asList(row.getId()), Arrays.asList("latitude", "longitude"));
            }
            if (distMeters < 10) {
                var message = "Survey coordinates less than 10m from site (" + String.format("%.1f", distMeters) + "m). " +
                        "This row will use the site's coordinates.";
                return new SurveyValidationError(ValidationCategory.DATA, ValidationLevel.WARNING, message,
                        Arrays.asList(row.getId()), Arrays.asList("latitude", "longitude"));
            }

        }

        return null;
    }
}
