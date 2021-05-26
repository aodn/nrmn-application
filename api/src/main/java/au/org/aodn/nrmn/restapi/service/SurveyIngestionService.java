package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.*;
import au.org.aodn.nrmn.restapi.util.OptionalUtil;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.utils.ImmutableMap;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SurveyIngestionService {
    private static final ImmutableMap<Integer, Integer> METHOD_ID_TO_MEASURE_ID_MAP = ImmutableMap
            .<Integer, Integer>builder().put(0, 1).put(1, 1).put(2, 4).put(3, 2).put(4, 3).put(5, 7).build();
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
    SiteRepository siteRepo;
    @Autowired
    EntityManager entityManager;
    @Autowired
    StagedJobLogRepository stagedJobLogRepository;
    @Autowired
    StagedJobRepository jobRepository;

    public Survey getSurvey(StagedRowFormatted stagedRow) {
        val site = stagedRow.getSite();

        val survey = Survey.builder().depth(stagedRow.getDepth()).surveyNum(stagedRow.getSurveyNum().orElse(null))
                .site(Site.builder().siteCode(site.getSiteCode()).build()).surveyDate(Date.valueOf(stagedRow.getDate()))
                .build();

        Optional<Survey> existingSurvey = surveyRepository.findOne(Example.of(survey));

        val siteSurveys = surveyRepository.findAll(Example
                .of(Survey.builder().site(Site.builder().siteCode(stagedRow.getSite().getSiteCode()).build()).build()));
        if (siteSurveys.isEmpty()) {
            site.setIsActive(true);
            siteRepo.save(site);
        }
        return existingSurvey.orElseGet(() -> surveyRepository.save(Survey.builder().depth(stagedRow.getDepth())
                .surveyNum(stagedRow.getSurveyNum().orElse(null)).direction(stagedRow.getDirection().toString())
                .site(site).surveyDate(Date.valueOf(stagedRow.getDate()))
                .surveyTime(Time.valueOf(stagedRow.getTime().orElse(LocalTime.NOON))).visibility(stagedRow.getVis().orElse(null))
                .program(stagedRow.getRef().getStagedJob().getProgram()).build()));
    }

    public SurveyMethod getSurveyMethod(StagedRowFormatted stagedRow) {
        Survey survey = getSurvey(stagedRow);

        boolean surveyNotDone = stagedRow.getCode().toLowerCase().equals("snd");
        Method method = entityManager.getReference(Method.class, stagedRow.getMethod());
        return SurveyMethod.builder().survey(survey).method(method).blockNum(stagedRow.getBlock())
                .surveyNotDone(surveyNotDone).build();
    }

    public List<Observation> getObservations(StagedRowFormatted stagedRow) {
        val surveyMethodExample = getSurveyMethod(stagedRow);
        val surveyMethod = surveyMethodRepository
                .findBySurveyIdMethodIdBlockNum(surveyMethodExample.getSurvey().getSurveyId(),
                        surveyMethodExample.getMethod().getMethodId(), surveyMethodExample.getBlockNum())
                .orElseGet(() -> surveyMethodRepository.save(surveyMethodExample));
        surveyMethod.getSurvey().getSurveyId();
        Diver diver = stagedRow.getDiver();
        Map<Integer, Integer> measures = stagedRow.getMeasureJson();

        Observation.ObservationBuilder baseObservationBuilder = Observation.builder().diver(diver)
                .surveyMethod(surveyMethod).observableItem(stagedRow.getSpecies());

        List<Observation> observations = measures.entrySet().stream().map(m -> {
            int measureId = METHOD_ID_TO_MEASURE_ID_MAP.get(stagedRow.getMethod());
            Measure measure = getMeasure(m.getKey(), measureId).get();

            return baseObservationBuilder.measure(measure).measureValue(m.getValue()).build();
        }).collect(Collectors.toList());

        observations.stream().map(obs -> obs.getSurveyMethod().getSurvey());
        return observations;
    }

    private Optional<Measure> getMeasure(Integer sequenceNumber, int measureId) {
        MeasureType measureType = MeasureType.builder().measureTypeId(measureId).build();

        Example<Measure> exampleMeasure = Example
                .of(Measure.builder().seqNo(sequenceNumber).measureType(measureType).build());
        val list = measureRepository.findAll(exampleMeasure);
        return list.stream().findFirst();
    }

    @Transactional
    public void ingestTransaction(StagedJob job, List<StagedRowFormatted> validatedRows) {
        List<Integer> surveyIds = validatedRows.stream().flatMap(row -> {
            return OptionalUtil.toStream(observationRepository.saveAll(getObservations(row)).stream().map(obs -> obs.getSurveyMethod().getSurvey()).findFirst());
        }).map(Survey::getSurveyId).distinct().collect(Collectors.toList());
        job.setStatus(StatusJobType.INGESTED);
        job.setSurveyIds(surveyIds);
        jobRepository.save(job);
    }
}
