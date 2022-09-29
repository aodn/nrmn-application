package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.data.model.Survey;
import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
    pattern = ".*TestData"))
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
class SurveyIT {
    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyTestData surveyTestData;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testMapping() {
        Survey survey = surveyTestData.persistedSurvey();
        entityManager.clear();
        Survey persistedSurvey = surveyRepository.findById(survey.getSurveyId()).get();

        // Compare value by value because the result may be in different order
        assertEquals(survey.getProgram().getProgramId(), persistedSurvey.getProgram().getProgramId());
        assertEquals(survey.getProgram().getProgramName(), persistedSurvey.getProgram().getProgramName());
        assertEquals(survey.getProgram().getIsActive(), persistedSurvey.getProgram().getIsActive());

        assertEquals(persistedSurvey.getSurveyMethods().size(), 0);

        assertEquals(survey.getSite().getSiteAttribute().get("ProxCountry"), persistedSurvey.getSite().getSiteAttribute().get("ProxCountry"));
        assertEquals(survey.getSite().getSiteAttribute().get("ProtectionStatus"), persistedSurvey.getSite().getSiteAttribute().get("ProtectionStatus"));

        assertEquals(survey.getSite().getGeom(), persistedSurvey.getSite().getGeom());
        assertEquals(survey.getSite().getIsActive(), persistedSurvey.getSite().getIsActive());
        assertEquals(survey.getSite().getCurrents(), persistedSurvey.getSite().getCurrents());
        assertEquals(survey.getSite().getWaveExposure(), persistedSurvey.getSite().getWaveExposure());
        assertEquals(survey.getSite().getSlope(), persistedSurvey.getSite().getSlope());
        assertEquals(survey.getSite().getRelief(), persistedSurvey.getSite().getRelief());
        assertEquals(survey.getSite().getProtectionStatus(), persistedSurvey.getSite().getProtectionStatus());
        assertEquals(survey.getSite().getMpa(), persistedSurvey.getSite().getMpa());
        assertEquals(survey.getSite().getLatitude(), persistedSurvey.getSite().getLatitude());
        assertEquals(survey.getSite().getLongitude(), persistedSurvey.getSite().getLongitude());
        assertEquals(survey.getSite().getCountry(), persistedSurvey.getSite().getCountry());
        assertEquals(survey.getSite().getState(), persistedSurvey.getSite().getState());
        assertEquals(survey.getSite().getSiteName(), persistedSurvey.getSite().getSiteName());
        assertEquals(survey.getSite().getSiteCode(), persistedSurvey.getSite().getSiteCode());
        assertEquals(survey.getSite().getSiteId(), persistedSurvey.getSite().getSiteId());

        assertEquals(survey.getSite().getLocation().getLocationId(), persistedSurvey.getSite().getLocation().getLocationId());
        assertEquals(survey.getSite().getLocation().getLocationName(), persistedSurvey.getSite().getLocation().getLocationName());
        assertEquals(survey.getSite().getLocation().getIsActive(), persistedSurvey.getSite().getLocation().getIsActive());

        assertEquals(survey.getSite().getOldSiteCodes().size(), persistedSurvey.getSite().getOldSiteCodes().size());
        assertEquals(survey.getSite().getOldSiteCodes().get(0), persistedSurvey.getSite().getOldSiteCodes().get(0));
        assertEquals(survey.getSite().getOldSiteCodes().get(1), persistedSurvey.getSite().getOldSiteCodes().get(1));

        assertEquals(survey.getProjectTitle(), persistedSurvey.getProjectTitle());
        assertEquals(survey.getBlockAbundanceSimulated(), persistedSurvey.getBlockAbundanceSimulated());
        assertEquals(survey.getPqDiverId(), persistedSurvey.getPqDiverId());
        assertEquals(survey.getPqZipUrl(), persistedSurvey.getPqZipUrl());
        assertEquals(survey.getPqCatalogued(), persistedSurvey.getPqCatalogued());
        assertEquals(survey.getNotes(), persistedSurvey.getNotes());
        assertEquals(survey.getInsideMarinePark(), persistedSurvey.getInsideMarinePark());
        assertEquals(survey.getProtectionStatus(), persistedSurvey.getProtectionStatus());
        assertEquals(survey.getLatitude(), persistedSurvey.getLatitude());
        assertEquals(survey.getLongitude(), persistedSurvey.getLongitude());
        assertEquals(survey.getDirection(), persistedSurvey.getDirection());
        assertEquals(survey.getVisibility(), persistedSurvey.getVisibility());
        assertEquals(survey.getDepth(), persistedSurvey.getDepth());

        assertEquals(survey.getSurveyDate(), persistedSurvey.getSurveyDate());
        assertEquals(survey.getSurveyTime(), persistedSurvey.getSurveyTime());
        assertEquals(survey.getCreated(), persistedSurvey.getCreated());
        assertEquals(survey.getUpdated(), persistedSurvey.getUpdated());
    }
}
