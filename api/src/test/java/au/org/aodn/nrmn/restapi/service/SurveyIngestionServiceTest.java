package au.org.aodn.nrmn.restapi.service;


import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.repository.MeasureRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyMethodRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import software.amazon.awssdk.utils.ImmutableMap;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SurveyIngestionServiceTest {
    @Mock
    EntityManager entityManager;
    @Mock
    SurveyRepository surveyRepository;
    @Mock
    SiteRepository siteRepo;
    @Mock
    SurveyMethodRepository surveyMethodRepository;
    @Mock
    MeasureRepository measureRepository;

    @InjectMocks
    SurveyIngestionService surveyIngestionService;
    StagedRowFormatted.StagedRowFormattedBuilder rowBuilder;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        when(surveyRepository.save(any())).then(s -> s.getArgument(0));
        when(siteRepo.save(any())).then(s -> s.getArgument(0));

        StagedRow ref = StagedRow.builder()
                .stagedJob(StagedJob.builder()
                        .program(Program.builder().programName("PROJECT")
                                .build()).build()).build();

        Diver diver = Diver.builder().initials("SAM").build();
        rowBuilder = StagedRowFormatted.builder()
                .block(1)
                .method(2)
                .diver(diver)
                .species(ObservableItem.builder().observableItemName("THE SPECIES").build())
                .site(Site.builder().siteCode("A SITE").build())
                .depth(1)
                .surveyNum(Optional.of(2))
                .direction(Directions.N)
                .vis(Optional.of(15))
                .date(LocalDate.of(2003, 03, 03))
                .time(Optional.of(LocalTime.of(12, 34, 56)))
                .pqs(diver)
                .isInvertSizing(Optional.of(true))
                .code("AAA")
                .measureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(3, 7).build())
                .ref(ref)
        ;
    }

    @Test
    void getSurveyForNewSurvey() {
        when(surveyRepository.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(siteRepo.save(any())).then(s -> s.getArgument(0));

        Survey survey = surveyIngestionService.getSurvey(rowBuilder.build());
        assertEquals(1, survey.getDepth());
        assertEquals(2, survey.getSurveyNum());
        assertEquals("A SITE", survey.getSite().getSiteCode());
        assertEquals("N", survey.getDirection());
        assertEquals(15, survey.getVisibility());
        assertEquals("2003-03-03", survey.getSurveyDate().toString());
        assertEquals("12:34:56", survey.getSurveyTime().toString());
    }

    /**
     * If an existing survey with the same depth, date and site no. exists then getSurvey should return it
     */
    @Test
    void getSurveyForExistingSurvey() {
        StagedRowFormatted row1 = rowBuilder.build();

        Survey survey1 = surveyIngestionService.getSurvey(row1);
        when(surveyRepository.findOne(any(Example.class))).thenReturn(Optional.of(survey1));

        StagedRowFormatted row2 = rowBuilder
                .block(2)
                .method(1)
                .measureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(3, 7).build())
                .build();

        Survey survey2 = surveyIngestionService.getSurvey(row2);

        assertEquals(survey1, survey2);
    }

    @Test
    void getSurveyMethod() {
        Method theMethod = Method.builder().methodId(2).methodName("The Method").isActive(true).build();
        when(entityManager.getReference(Method.class, 2)).thenReturn(theMethod);
        when(surveyMethodRepository.save(any())).then(s -> s.getArgument(0));
        StagedRowFormatted row = rowBuilder.build();
        Survey survey = surveyIngestionService.getSurvey(row);
        SurveyMethod surveyMethod = surveyIngestionService.getSurveyMethod(survey, row);
        assertEquals(1, surveyMethod.getBlockNum());
        assertEquals(2, surveyMethod.getMethod().getMethodId());
        assertEquals(surveyIngestionService.getSurvey(row), surveyMethod.getSurvey());
        assertEquals(false, surveyMethod.getSurveyNotDone());
    }

    @Test
    void getSurveyMethodNotDone() {
        Method theMethod = Method.builder().methodId(2).methodName("The Method").isActive(true).build();
        when(entityManager.getReference(Method.class, 2)).thenReturn(theMethod);
        when(surveyMethodRepository.save(any())).then(s -> s.getArgument(0));
        StagedRowFormatted row = rowBuilder.code("snd").build();
        Survey survey = surveyIngestionService.getSurvey(row);
        SurveyMethod surveyMethod = surveyIngestionService.getSurveyMethod(survey, row);
        assertEquals(true, surveyMethod.getSurveyNotDone());
    }

    @Test
    void getObservationsForSameSurveyReturnsSameSurvey() {
        Method theMethod = Method.builder().methodId(2).methodName("The Method").isActive(true).build();
        when(entityManager.getReference(Method.class, 2)).thenReturn(theMethod);
        when(measureRepository.findById(any(Integer.class))).then(m -> Optional.of(Measure.builder().build()));
        when(surveyMethodRepository.save(any())).then(s -> s.getArgument(0));

        StagedRowFormatted row = rowBuilder.build();
        StagedRowFormatted rowFromSameSurvey = rowBuilder
            .measureJson(ImmutableMap.<Integer, Integer>builder().put(1, 10).put(3, 11).build()).build();

        Survey survey = surveyIngestionService.getSurvey(row);
        SurveyMethod surveyMethod = surveyIngestionService.getSurveyMethod(survey, row);

        List<Observation> observations = surveyIngestionService.getObservations(surveyMethod, row);
        List<Observation> observationsFromSameSurvey = surveyIngestionService.getObservations(surveyMethod,
                rowFromSameSurvey);
        assertEquals(
            observations.get(0).getSurveyMethod().getSurvey(),
            observationsFromSameSurvey.get(0).getSurveyMethod().getSurvey());
    }

    @Test
    void getObservationsDifferentSurveysForDifferentDepths() {
        when(measureRepository.findById(any(Integer.class))).then(m -> Optional.of(Measure.builder().build()));
        when(surveyMethodRepository.save(any())).then(s -> s.getArgument(0));

        Method theMethod = Method.builder().methodId(2).methodName("The Method").isActive(true).build();
        when(entityManager.getReference(Method.class, 2)).thenReturn(theMethod);

        StagedRowFormatted row = rowBuilder.build();
        StagedRowFormatted rowWithDifferentDepth = rowBuilder
                .depth(5)
                .surveyNum(Optional.of(3))
                .measureJson(ImmutableMap.<Integer, Integer>builder().put(1, 10).put(3, 11).build())
                .build();

        Survey survey = surveyIngestionService.getSurvey(row);
        SurveyMethod surveyMethod = surveyIngestionService.getSurveyMethod(survey, row);
        List<Observation> observations = surveyIngestionService.getObservations(surveyMethod, row);

        Survey differentSurvey = surveyIngestionService.getSurvey(rowWithDifferentDepth);
        SurveyMethod differentSurveyMethod = surveyIngestionService.getSurveyMethod(differentSurvey, rowWithDifferentDepth);
        List<Observation> observationsFromDifferentSurvey =
         surveyIngestionService.getObservations(differentSurveyMethod, rowWithDifferentDepth);

        assertNotEquals(
                observations.get(0).getSurveyMethod().getSurvey(),
                observationsFromDifferentSurvey.get(0).getSurveyMethod().getSurvey());
    }

    @Test
    void getObservationsDifferentSurveysForDifferentDate() {
        when(measureRepository.findById(any(Integer.class))).then(m -> Optional.of(Measure.builder().build()));
        when(surveyMethodRepository.save(any())).then(s -> s.getArgument(0));

        Method theMethod = Method.builder().methodId(2).methodName("The Method").isActive(true).build();
        when(entityManager.getReference(Method.class, 2)).thenReturn(theMethod);

        StagedRowFormatted row = rowBuilder.build();
        StagedRowFormatted rowWithDifferentDate = rowBuilder
                .date(LocalDate.of(1999, 9, 9))
                .measureJson(ImmutableMap.<Integer, Integer>builder().put(1, 10).put(3, 11).build())
                .build();

        Survey survey = surveyIngestionService.getSurvey(row);
        SurveyMethod surveyMethod = surveyIngestionService.getSurveyMethod(survey, row);
        List<Observation> observations = surveyIngestionService.getObservations(surveyMethod, row);

        Survey differentSurvey = surveyIngestionService.getSurvey(rowWithDifferentDate);
        SurveyMethod differentSurveyMethod = surveyIngestionService.getSurveyMethod(differentSurvey, rowWithDifferentDate);
        List<Observation> observationsFromDifferentSurvey =
                surveyIngestionService.getObservations(differentSurveyMethod, rowWithDifferentDate);

        assertNotEquals(
                observations.get(0).getSurveyMethod().getSurvey(),
                observationsFromDifferentSurvey.get(0).getSurveyMethod().getSurvey());
    }

    @Test
    void getObservationsDifferentSurveysForDifferentSites() {
        when(measureRepository.findById(any(Integer.class))).then(m -> Optional.of(Measure.builder().build()));
        when(surveyMethodRepository.save(any())).then(s -> s.getArgument(0));

        Method theMethod = Method.builder().methodId(2).methodName("The Method").isActive(true).build();
        when(entityManager.getReference(Method.class, 2)).thenReturn(theMethod);

        StagedRowFormatted row = rowBuilder.build();
        StagedRowFormatted rowWithDifferentSite = rowBuilder
                .site(Site.builder().siteName("A DIFFERENT SITE").build())
                .measureJson(ImmutableMap.<Integer, Integer>builder().put(1, 10).put(3, 11).build())
                .build();

        Survey survey = surveyIngestionService.getSurvey(row);
        SurveyMethod surveyMethod = surveyIngestionService.getSurveyMethod(survey, row);
        List<Observation> observations = surveyIngestionService.getObservations(surveyMethod, row);

        Survey surveyWithDifferentSite = surveyIngestionService.getSurvey(rowWithDifferentSite);
        SurveyMethod surveyMethodForDifferentSite = surveyIngestionService.getSurveyMethod(surveyWithDifferentSite,
         rowWithDifferentSite);
        List<Observation> observationsFromDifferentSurvey =
         surveyIngestionService.getObservations(surveyMethodForDifferentSite, rowWithDifferentSite);

        assertNotEquals(
                observations.get(0).getSurveyMethod().getSurvey(),
                observationsFromDifferentSurvey.get(0).getSurveyMethod().getSurvey());
    }
}
