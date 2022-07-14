package au.org.aodn.nrmn.restapi.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.Program;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.SurveyMethodEntity;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.repository.MeasureRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyMethodRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import software.amazon.awssdk.utils.ImmutableMap;

@ExtendWith(MockitoExtension.class)
public class SurveyCorrectionServiceTest {
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
    @Mock
    ObservationRepository observationRepository;
    @Mock
    StagedJobRepository jobRepository;
    @Mock
    private StagedJobLogRepository stagedJobLogRepository;

    @InjectMocks
    SurveyCorrectionService surveyCorrectionService;
    StagedRowFormatted.StagedRowFormattedBuilder rowBuilder;

    @BeforeEach
    void init() {

        StagedRow ref = StagedRow.builder()
                .stagedJob(StagedJob.builder().program(Program.builder().programName("PROJECT").build()).build())
                .build();

        Diver diver = Diver.builder().initials("SAM").build();
        rowBuilder = StagedRowFormatted.builder().block(1).method(2).diver(diver)
                .species(Optional.of(ObservableItem.builder().observableItemName("THE SPECIES").build()))
                .site(Site.builder().siteCode("A SITE").isActive(false).build()).depth(1).surveyNum(2)
                .direction(Directions.N).vis(Optional.of(15.5)).date(LocalDate.of(2003, 03, 03))
                .time(Optional.of(LocalTime.of(12, 34, 56))).pqs(diver).isInvertSizing(true).code("AAA")
                .inverts(0)
                .measureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(3, 7).build()).ref(ref);
    }

    @Test
    void correctSurvey() {
        when(surveyMethodRepository.save(any())).then(s -> s.getArgument(0));
        when(observationRepository.saveAll(any())).then(s -> s.getArgument(0));

        Program program = Program.builder().programId(1).build();
        Survey ingestedSurvey = Survey.builder().surveyId(1).build();
        StagedJob ingestedJob = StagedJob.builder().id(1L).program(program).build();
        StagedRow ingestedRow = StagedRow.builder().stagedJob(ingestedJob).species("Survey Not Done").build();

        StagedRowFormatted correctedRow1 = StagedRowFormatted
                .builder()
                .ref(ingestedRow)
                .site(Site.builder().siteCode("test1").isActive(true).build())
                .date(LocalDate.parse("2018-12-27"))
                .time(Optional.empty())
                .depth(10)
                .surveyNum(1)
                .direction(Directions.N)
                .vis(Optional.of(2.5))
                .method(2)
                .block(1)
                .code("snd")
                .species(Optional.empty())
                .measureJson(Collections.emptyMap())
                .build();

        StagedRowFormatted correctedRow2 = StagedRowFormatted
                .builder()
                .ref(ingestedRow)
                .site(Site.builder().siteCode("test1").isActive(true).build())
                .date(LocalDate.parse("2018-12-27"))
                .time(Optional.empty())
                .depth(10)
                .surveyNum(1)
                .direction(Directions.N)
                .vis(Optional.of(2.5))
                .method(2)
                .block(1)
                .code("snd")
                .species(Optional.empty())
                .measureJson(Collections.emptyMap())
                .build();

        surveyCorrectionService.correctSurvey(ingestedJob, ingestedSurvey, Arrays.asList(correctedRow1, correctedRow2));

        ArgumentCaptor<Survey> surveyCaptor = ArgumentCaptor.forClass(Survey.class);
        // Mockito.verify(surveyRepository).save(surveyCaptor.capture());

        Survey survey = surveyCaptor.getValue();
        assertEquals(3.0, survey.getVisibility());

        ArgumentCaptor<SurveyMethodEntity> surveyMethodCaptor = ArgumentCaptor.forClass(SurveyMethodEntity.class);
        Mockito.verify(surveyMethodRepository).save(surveyMethodCaptor.capture());

        SurveyMethodEntity surveyMethod = surveyMethodCaptor.getValue();
        assertEquals(true, surveyMethod.getSurveyNotDone());
    }

    @Test
    void deleteSurvey() {

    }
}
