package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.repository.*;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.utils.ImmutableMap;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SurveyIngestionService {
    private static final ImmutableMap<Integer, Integer> METHOD_ID_TO_MEASURE_ID_MAP = ImmutableMap.<Integer, Integer>builder()
            .put(0,1).put(1, 1).put(2, 4).put(3, 2).put(4, 3).put(5, 7).build();
    private static Logger logger = LoggerFactory.getLogger(SurveyIngestionService.class);
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    MethodRepository methodRepository;
    @Autowired
    MeasureRepository measureRepository;
    @Autowired
    ObservationRepository observationRepository;
    @Autowired
    SurveyMethodRepository surveyMethodRepository;
    @Autowired
    ObservableItemRepository observableItemRepository;
    @Autowired
    ProgramRepository programRepository;
    @Autowired
    EntityManager entityManager;

    public List<Observation> ingestStagedRow(StagedRowFormatted stagedRow) {
        return observationRepository.saveAll(getObservations(stagedRow));
    }

    public Survey getSurvey(StagedRowFormatted stagedRow) {
        val survey = Survey.builder()
                .depth(stagedRow.getDepth())
                .surveyNum(stagedRow.getSurveyNum().orElse(null))
                .site(Site.builder().siteCode(stagedRow.getSite().getSiteCode()).build())
                .surveyDate(Date.valueOf(stagedRow.getDate()))
                .build();

        Optional<Survey> existingSurvey = surveyRepository.findOne(Example.of(survey));

        return existingSurvey.orElseGet(() -> surveyRepository.save(Survey.builder()
                .depth(stagedRow.getDepth())
                .surveyNum(stagedRow.getSurveyNum().orElse(null))
                .direction(stagedRow.getDirection().toString())
                .site(stagedRow.getSite())
                .surveyDate(Date.valueOf(stagedRow.getDate()))
                .surveyTime(Time.valueOf(stagedRow.getTime().orElse(null)))
                .visibility(stagedRow.getVis().orElse(null))
                .program(stagedRow.getRef().getStagedJob().getProgram())
                .build()));
    }

    public SurveyMethod getSurveyMethod(StagedRowFormatted stagedRow) {
        Survey survey = getSurvey(stagedRow);

        boolean surveyNotDone = stagedRow.getCode().toLowerCase().equals("snd");
        Method method = entityManager.getReference(Method.class, stagedRow.getMethod());
        return SurveyMethod.builder()
                .survey(survey)
                .method(method)
                .blockNum(stagedRow.getBlock())
                .surveyNotDone(surveyNotDone)
                .build();
    }

    public List<Observation> getObservations(StagedRowFormatted stagedRow) {
        val surveyMethodExample = getSurveyMethod(stagedRow);
        val surveyMethod = surveyMethodRepository.findOne(Example.of(surveyMethodExample)).orElseGet(() ->
                surveyMethodRepository.save(surveyMethodExample)
        );
        surveyMethod.getSurvey().getSurveyId();
        Diver diver = stagedRow.getDiver();
        Map<Integer, Integer> measures = stagedRow.getMeasureJson();

        Observation.ObservationBuilder baseObservationBuilder = Observation.builder()
                .diver(diver)
                .surveyMethod(surveyMethod)
                .observableItem(stagedRow.getSpecies().get());


        List<Observation> observations = measures.entrySet().stream().map(m -> {
            int measureId = METHOD_ID_TO_MEASURE_ID_MAP.get(stagedRow.getMethod());
            Measure measure = getMeasure(m.getKey(), measureId).get();

            return baseObservationBuilder
                    .measure(measure)
                    .measureValue(m.getValue())
                    .build();
        }).collect(Collectors.toList());

        observations.stream().map(obs -> obs.getSurveyMethod().getSurvey());
        return observations;
    }

    private Optional<Measure> getMeasure(Integer sequenceNumber, int measureId) {
        MeasureType measureType = MeasureType.builder().measureTypeId(measureId).build();

        Example<Measure> exampleMeasure = Example.of(Measure.builder()
                .seqNo(sequenceNumber)
                .measureType(measureType)
                .build());
        val list = measureRepository.findAll(exampleMeasure);
        return list.stream().findFirst();
    }
}
