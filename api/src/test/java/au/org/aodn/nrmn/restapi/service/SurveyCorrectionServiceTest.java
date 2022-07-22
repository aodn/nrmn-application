package au.org.aodn.nrmn.restapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.ObsItemType;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.Observation;
import au.org.aodn.nrmn.restapi.model.db.Program;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.repository.MeasureRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyMethodRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

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

    @Captor
    private ArgumentCaptor<List<Observation>> observationCaptor;

    StagedRowFormatted.StagedRowFormattedBuilder rowBuilder;
    Map<Integer, Integer> startingMeasures;
    Survey ingestedSurvey;
    StagedJob ingestedJob;

    @BeforeEach
    void init() {

        var program = Program.builder().programName("PROJECT").programId(1).build();
        var job = StagedJob.builder().program(program).build();
        var ref = StagedRow.builder().stagedJob(job).build();
        var diver = Diver.builder().initials("SAM").build();
        var obsItemType = ObsItemType.builder().obsItemTypeId(1).build();
        var observableItem = ObservableItem.builder().obsItemType(obsItemType).observableItemName("THE SPECIES").build();
        ingestedSurvey = Survey.builder().surveyId(1).build();
        ingestedJob = StagedJob.builder().id(1L).program(program).build();

        startingMeasures = Map.of(1, 4, 3, 7);

        rowBuilder = StagedRowFormatted.builder().block(1).method(2).diver(diver)
                .species(Optional.of(observableItem))
                .site(Site.builder().siteCode("A SITE").isActive(false).build()).depth(1).surveyNum(2)
                .direction(Directions.N).vis(Optional.of(15.5)).date(LocalDate.of(2003, 03, 03))
                .time(Optional.of(LocalTime.of(12, 34, 56))).pqs(diver).isInvertSizing(true).code("AAA")
                .inverts(0)
                .measureJson(startingMeasures)
                .ref(ref);
    }

    @Test
    void correctSurvey() {

        when(surveyMethodRepository.save(any())).then(s -> s.getArgument(0));
        when(observationRepository.saveAll(any())).then(s -> s.getArgument(0));

        var correctedRow = rowBuilder.build();
        var correctedMeasures = Map.of(2, 5);
        correctedRow.setMeasureJson(correctedMeasures);

        try {
            surveyCorrectionService.correctSurvey(ingestedJob, ingestedSurvey, Arrays.asList(correctedRow));
        } catch (Exception e) {
            fail(e.getMessage());
        }

        Mockito.verify(observationRepository).saveAll(observationCaptor.capture());
        var savedObservations = observationCaptor.getValue();
        assertEquals(1, savedObservations.size());
        assertEquals(correctedMeasures.get(2), savedObservations.get(0).getMeasureValue());

        Mockito.verify(surveyMethodRepository).deleteForSurveyId(ingestedSurvey.getSurveyId());
    }

    @Test
    void deleteSurvey() {
        when(observationRepository.findObservationIdsForSurvey(any())).thenReturn(Arrays.asList(0));
        try {
            surveyCorrectionService.deleteSurvey(ingestedJob, ingestedSurvey, Arrays.asList());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        Mockito.verify(surveyRepository, times(1)).deleteById(1);
    }
}
