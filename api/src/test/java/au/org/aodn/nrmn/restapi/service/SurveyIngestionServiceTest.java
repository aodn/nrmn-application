package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.repository.*;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import software.amazon.awssdk.utils.ImmutableMap;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static au.org.aodn.nrmn.restapi.service.SurveyIngestionService.MEASURE_TYPE_FISH_SIZE_CLASS;
import static au.org.aodn.nrmn.restapi.service.SurveyIngestionService.MEASURE_TYPE_SINGLE_ITEM;
import static au.org.aodn.nrmn.restapi.service.SurveyIngestionService.OBS_ITEM_TYPE_DEBRIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Mock
    ObservationRepository observationRepository;
    @Mock
    StagedJobRepository jobRepository;

    @InjectMocks
    SurveyIngestionService surveyIngestionService;
    StagedRowFormatted.StagedRowFormattedBuilder rowBuilder;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);

        StagedRow ref = StagedRow.builder()
                .stagedJob(StagedJob.builder().program(Program.builder().programName("PROJECT").build()).build())
                .build();

        Diver diver = Diver.builder().initials("SAM").build();
        rowBuilder = StagedRowFormatted.builder().block(1).method(2).diver(diver)
                .species(Optional.of(ObservableItem.builder().observableItemName("THE SPECIES").build()))
                .site(Site.builder().siteCode("A SITE").isActive(false).build()).depth(1).surveyNum(Optional.of(2))
                .direction(Directions.N).vis(Optional.of(15)).date(LocalDate.of(2003, 03, 03))
                .time(Optional.of(LocalTime.of(12, 34, 56))).pqs(diver).isInvertSizing(Optional.of(true)).code("AAA")
                .inverts(0)
                .measureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(3, 7).build()).ref(ref);
    }

    @Test
    void getSurveyForNewSurvey() {
        when(surveyRepository.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(surveyRepository.save(any())).then(s -> s.getArgument(0));
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
     * If an existing survey with the same depth, date and site no. exists then
     * getSurvey should return it
     */
    @Test
    void getSurveyForExistingSurvey() {
        when(surveyRepository.save(any())).then(s -> s.getArgument(0));
        when(siteRepo.save(any())).then(s -> s.getArgument(0));

        StagedRowFormatted row1 = rowBuilder.build();

        Survey survey1 = surveyIngestionService.getSurvey(row1);
        when(surveyRepository.findOne(any(Example.class))).thenReturn(Optional.of(survey1));

        StagedRowFormatted row2 = rowBuilder.block(2).method(1)
                .measureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(3, 7).build()).build();

        Survey survey2 = surveyIngestionService.getSurvey(row2);

        assertEquals(survey1, survey2);
    }

    @Test
    void getSurveyMethod() {
        Method theMethod = Method.builder().methodId(2).methodName("The Method").isActive(true).build();
        when(entityManager.getReference(Method.class, 2)).thenReturn(theMethod);
        when(surveyMethodRepository.save(any())).then(s -> s.getArgument(0));
        StagedRowFormatted row = rowBuilder.build();
        Survey survey = Survey.builder().surveyId(1).build();
        SurveyMethod surveyMethod = surveyIngestionService.getSurveyMethod(survey, row);
        assertEquals(1, surveyMethod.getBlockNum());
        assertEquals(2, surveyMethod.getMethod().getMethodId());
        assertEquals(survey, surveyMethod.getSurvey());
        assertEquals(false, surveyMethod.getSurveyNotDone());
    }

    @Test
    void getSurveyMethodNotDone() {
        Method theMethod = Method.builder().methodId(2).methodName("The Method").isActive(true).build();
        when(entityManager.getReference(Method.class, 2)).thenReturn(theMethod);
        when(surveyMethodRepository.save(any())).then(s -> s.getArgument(0));
        StagedRowFormatted row = rowBuilder.code("snd").build();
        Survey survey = Survey.builder().surveyId(1).build();
        SurveyMethod surveyMethod = surveyIngestionService.getSurveyMethod(survey, row);
        assertEquals(true, surveyMethod.getSurveyNotDone());
    }

    @Test
    void getObservations() {
        when(measureRepository.findByMeasureTypeIdAndSeqNo(1, 1)).then(m -> Optional.of(Measure.builder()
                .measureName("2.5cm").measureType(MeasureType.builder().measureTypeId(1).build()).build()));
        when(measureRepository.findByMeasureTypeIdAndSeqNo(1, 3)).then(m -> Optional.of(Measure.builder()
                .measureName("10.5cm").measureType(MeasureType.builder().measureTypeId(1).build()).build()));

        // M0, M1, M2, M7, M10, M11
        IntStream.of(0, 1, 2, 7, 10, 11).forEach(i -> {
            ObservableItem obsItem = ObservableItem.builder()
                    .obsItemType(ObsItemType.builder().obsItemTypeId(1).build()).build();
            StagedRowFormatted row =
             rowBuilder.isInvertSizing(Optional.of(true)).species(Optional.of(obsItem)).method(i).build();
            Survey survey = Survey.builder().surveyId(i).build();
            Method theMethod = Method.builder().methodId(i).methodName("The Method").isActive(true).build();
            SurveyMethod surveyMethod = SurveyMethod.builder().survey(survey).method(theMethod).blockNum(1).build();

            // ExtendedSizing = false
            List<Observation> observations = surveyIngestionService.getObservations(surveyMethod, row, false);
            assertEquals(1, observations.get(0).getMeasure().getMeasureType().getMeasureTypeId());
            assertEquals(2, observations.size());
            assertEquals(surveyMethod, observations.get(0).getSurveyMethod());
            assertEquals(row.getDiver(), observations.get(0).getDiver());
            assertEquals("2.5cm", observations.get(0).getMeasure().getMeasureName());
            assertEquals(4, observations.get(0).getMeasureValue());
            assertEquals(surveyMethod, observations.get(1).getSurveyMethod());
            assertEquals(row.getDiver(), observations.get(1).getDiver());
            assertEquals("10.5cm", observations.get(1).getMeasure().getMeasureName());
            assertEquals(7, observations.get(1).getMeasureValue());
        });
    }

    @Test
    void getObservationsM3() {
        when(measureRepository.findByMeasureTypeIdAndSeqNo(2, 1)).then(m -> Optional.of(Measure.builder()
                .measureName("2.5cm").measureType(MeasureType.builder().measureTypeId(2).build()).build()));
        SurveyMethod surveyMethod3 = SurveyMethod.builder().survey(Survey.builder().surveyId(3).build())
                .method(Method.builder().methodId(3).methodName("").isActive(true).build()).blockNum(1).build();
        List<Observation> observations3 = surveyIngestionService.getObservations(surveyMethod3,
                rowBuilder.isInvertSizing(Optional.of(true)).method(3).species(Optional.of(
                        ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(1).build()).build()))
                        .build(),
                false);
        // M3 should be mapped to measure_type_id = 2 - In Situ Quadrats
        assertEquals(2, observations3.get(0).getMeasure().getMeasureType().getMeasureTypeId());
    }

    @Test
    void getObservationsM4() {
        when(measureRepository.findByMeasureTypeIdAndSeqNo(3, 1)).then(m -> Optional.of(Measure.builder()
                .measureName("2.5cm").measureType(MeasureType.builder().measureTypeId(3).build()).build()));
        SurveyMethod surveyMethod4 = SurveyMethod.builder().survey(Survey.builder().surveyId(4).build())
                .method(Method.builder().methodId(4).methodName("").isActive(true).build()).blockNum(1).build();
        List<Observation> observations4 = surveyIngestionService.getObservations(surveyMethod4,
                rowBuilder.isInvertSizing(Optional.of(true)).method(4).species(Optional.of(
                        ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(1).build()).build()))
                        .build(),
                false);
        // M4 should be mapped to measure_type_id = 3 - Macrocystis Block
        assertEquals(3, observations4.get(0).getMeasure().getMeasureType().getMeasureTypeId());
    }

    @Test
    void getObservationsM5() {
        when(measureRepository.findByMeasureTypeIdAndSeqNo(7, 1)).then(m -> Optional.of(Measure.builder()
                .measureName("2.5cm").measureType(MeasureType.builder().measureTypeId(7).build()).build()));
        SurveyMethod surveyMethod5 = SurveyMethod.builder().survey(Survey.builder().surveyId(5).build())
                .method(Method.builder().methodId(4).methodName("").isActive(true).build()).blockNum(1).build();
        List<Observation> observations5 = surveyIngestionService.getObservations(surveyMethod5,
                rowBuilder.isInvertSizing(Optional.of(true)).method(5).species(Optional.of(
                        ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(1).build()).build()))
                        .build(),
                false);
        // M4 should be mapped to measure_type_id = 7 - Limpet Quadrat
        assertEquals(7, observations5.get(0).getMeasure().getMeasureType().getMeasureTypeId());
    }

    @Test
    void getObservationsWithExtendedSizing() {
        when(measureRepository.findByMeasureTypeIdAndSeqNo(4, 1)).then(m -> Optional.of(Measure.builder()
                .measureName("0.5cm").measureType(MeasureType.builder().measureTypeId(4).build()).build()));
        when(measureRepository.findByMeasureTypeIdAndSeqNo(4, 3)).then(m -> Optional.of(Measure.builder()
                .measureName("1.5cm").measureType(MeasureType.builder().measureTypeId(4).build()).build()));

        Optional<ObservableItem> obsItem =
         Optional.of(ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(1).build())
                .build());
        StagedRowFormatted row = rowBuilder.isInvertSizing(Optional.of(true)).species(obsItem).build();

        // M2
        Survey survey = Survey.builder().surveyId(1).build();
        Method theMethod = Method.builder().methodId(2).methodName("The Method").isActive(true).build();
        SurveyMethod surveyMethod = SurveyMethod.builder().survey(survey).method(theMethod).blockNum(1).build();

        // ExtendedSizing = false
        List<Observation> observations = surveyIngestionService.getObservations(surveyMethod, row, true);
        assertEquals(4, observations.get(0).getMeasure().getMeasureType().getMeasureTypeId());
        assertEquals(2, observations.size());
        assertEquals(surveyMethod, observations.get(0).getSurveyMethod());
        assertEquals(row.getDiver(), observations.get(0).getDiver());
        assertEquals("0.5cm", observations.get(0).getMeasure().getMeasureName());
        assertEquals(4, observations.get(0).getMeasureValue());
        assertEquals(surveyMethod, observations.get(1).getSurveyMethod());
        assertEquals(row.getDiver(), observations.get(1).getDiver());
        assertEquals("1.5cm", observations.get(1).getMeasure().getMeasureName());
        assertEquals(7, observations.get(1).getMeasureValue());
    }

    @Test
    void getInvertsObservationM1() {
        Measure unsized = Measure.builder()
                                 .measureName("Unsized")
                                 .measureType(MeasureType.builder().measureTypeId(MEASURE_TYPE_FISH_SIZE_CLASS).build())
                                 .build();
        when(measureRepository.findByMeasureTypeIdAndSeqNo(MEASURE_TYPE_FISH_SIZE_CLASS, 0)).then(m -> Optional.of(unsized));
        SurveyMethod surveyMethod6 = SurveyMethod.builder().survey(Survey.builder().surveyId(6).build())
                .method(Method.builder().methodId(1).methodName("").isActive(true).build()).blockNum(1).build();
        List<Observation> observations6 = surveyIngestionService.getObservations(surveyMethod6,
                rowBuilder.inverts(10).measureJson(Collections.emptyMap()).isInvertSizing(Optional.empty()).method(1).species(
                        Optional.of(ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(1).build()).build()))
                          .build(),
                false);
        // Should return one observation of 10 unsized species
        assertEquals(1, observations6.size());
        assertEquals("Unsized", observations6.get(0).getMeasure().getMeasureName());
        assertEquals(10, observations6.get(0).getMeasureValue());
    }

    @Test
    void getInvertsObservationDebris() {
        Measure item = Measure.builder()
                                 .measureName("Item")
                                 .measureType(MeasureType.builder().measureTypeId(MEASURE_TYPE_SINGLE_ITEM).build())
                                 .build();
        when(measureRepository.findByMeasureTypeIdAndSeqNo(MEASURE_TYPE_SINGLE_ITEM, 1)).then(m -> Optional.of(item));
        SurveyMethod surveyMethod7 = SurveyMethod.builder().survey(Survey.builder().surveyId(7).build())
                .method(Method.builder().methodId(2).methodName("").isActive(true).build()).blockNum(1).build();
        List<Observation> observations7 = surveyIngestionService.getObservations(surveyMethod7,
                rowBuilder.inverts(10).measureJson(Collections.emptyMap()).isInvertSizing(Optional.empty()).method(1).species(
                        Optional.of(ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(OBS_ITEM_TYPE_DEBRIS).build()).build()))
                          .build(),
                false);
        // Should return one observation of 10 items
        assertEquals(1, observations7.size());
        assertEquals("Item", observations7.get(0).getMeasure().getMeasureName());
        assertEquals(10, observations7.get(0).getMeasureValue());
    }

    @Test
    void ingestSurveyNotDone() {
        when(surveyRepository.save(any())).then(s -> s.getArgument(0));
        when(surveyMethodRepository.save(any())).then(s -> s.getArgument(0));
        when(observationRepository.saveAll(any())).then(s -> s.getArgument(0));

        Program program = Program.builder().programId(1).build();
        StagedJob stagedJob = StagedJob.builder().id(1L).program(program).build();
        StagedRow stagedRow = StagedRow.builder().stagedJob(stagedJob).build();
        StagedRowFormatted formattedRow = StagedRowFormatted
                .builder()
                .ref(stagedRow)
                .site(Site.builder().siteCode("test1").isActive(true).build())
                .date(LocalDate.parse("2018-12-27"))
                .time(Optional.empty())
                .depth(10)
                .surveyNum(Optional.of(1))
                .direction(Directions.N)
                .vis(Optional.empty())
                .method(2)
                .block(1)
                .code("snd")
                .species(Optional.empty())
                .measureJson(Collections.emptyMap())
                .build();

        surveyIngestionService.ingestTransaction(stagedJob, Collections.singletonList(formattedRow));

        ArgumentCaptor<SurveyMethod> surveyMethodCaptor = ArgumentCaptor.forClass(SurveyMethod.class);
        Mockito.verify(surveyMethodRepository).save(surveyMethodCaptor.capture());
        SurveyMethod surveyMethod = surveyMethodCaptor.getValue();
        assertEquals(true, surveyMethod.getSurveyNotDone());

    }

    @Test
    void getObservationsDebrisSavedAsM12() {

        ObservableItem debrisItem = ObservableItem.builder().obsItemType(ObsItemType.builder().obsItemTypeId(5).build()).build();
        StagedRowFormatted row = rowBuilder.isInvertSizing(Optional.of(true)).species(Optional.of(debrisItem)).build();

        StagedRowFormatted groupedRow = surveyIngestionService.groupRowsBySurveyMethod(Arrays.asList(row)).values().iterator().next().get(0);

        // Debris are entered as M2 but stored as M12
        assertEquals(12, groupedRow.getMethod());
    }
}
