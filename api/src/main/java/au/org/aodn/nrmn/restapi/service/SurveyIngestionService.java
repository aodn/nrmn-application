package au.org.aodn.nrmn.restapi.service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.db.model.Diver;
import au.org.aodn.nrmn.db.model.Measure;
import au.org.aodn.nrmn.db.model.Method;
import au.org.aodn.nrmn.db.model.Observation;
import au.org.aodn.nrmn.db.model.Program;
import au.org.aodn.nrmn.db.model.Site;
import au.org.aodn.nrmn.db.model.StagedJob;
import au.org.aodn.nrmn.db.model.StagedJobLog;
import au.org.aodn.nrmn.db.model.Survey;
import au.org.aodn.nrmn.db.model.SurveyMethodEntity;
import au.org.aodn.nrmn.db.model.enums.MeasureType;
import au.org.aodn.nrmn.db.model.enums.ObservableItemType;
import au.org.aodn.nrmn.db.model.enums.StagedJobEventType;
import au.org.aodn.nrmn.db.model.enums.StatusJobType;
import au.org.aodn.nrmn.db.model.enums.SurveyMethod;
import au.org.aodn.nrmn.db.repository.MeasureRepository;
import au.org.aodn.nrmn.db.repository.MethodRepository;
import au.org.aodn.nrmn.db.repository.ObservableItemRepository;
import au.org.aodn.nrmn.db.repository.ObservationRepository;
import au.org.aodn.nrmn.db.repository.ProgramRepository;
import au.org.aodn.nrmn.db.repository.SiteRepository;
import au.org.aodn.nrmn.db.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.db.repository.StagedJobRepository;
import au.org.aodn.nrmn.db.repository.SurveyMethodRepository;
import au.org.aodn.nrmn.db.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import lombok.Value;

@Service
public class SurveyIngestionService {

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

    public Survey getSurvey(Program program, OptionalDouble visAvg, StagedRowFormatted stagedRow) {

        Site site = stagedRow.getSite();

        if (!site.getIsActive()) {
            site.setIsActive(true);
            siteRepo.save(site);
        }

        Survey survey = Survey.builder().program(program).depth(stagedRow.getDepth()).surveyNum(stagedRow.getSurveyNum())
                .site(Site.builder().siteCode(site.getSiteCode()).build()).surveyDate(Date.valueOf(stagedRow.getDate()))
                .build();

        Optional<Survey> existingSurvey = surveyRepository.findOne(Example.of(survey));

        return existingSurvey.orElseGet(() -> surveyRepository.save(
                Survey.builder()
                        .depth(stagedRow.getDepth())
                        .surveyNum(stagedRow.getSurveyNum())
                        .direction(stagedRow.getDirection() != null ? stagedRow.getDirection().toString() : null)
                        .site(site).surveyDate(Date.valueOf(stagedRow.getDate()))
                        .surveyTime(Time.valueOf(stagedRow.getTime().orElse(LocalTime.NOON)))
                        .visibility(visAvg.isPresent() ? visAvg.getAsDouble() : null)
                        .program(stagedRow.getRef().getStagedJob().getProgram())
                        .protectionStatus(site.getProtectionStatus())
                        .insideMarinePark(StringUtils.isNotBlank(site.getMpa()) ? "Yes" : "No")
                        .longitude(stagedRow.getLongitude())
                        .latitude(stagedRow.getLatitude())
                        .pqDiverId(stagedRow.getPqs() != null ? stagedRow.getPqs().getDiverId() : null)
                        .build()));
    }

    public SurveyMethodEntity getSurveyMethod(Survey survey, StagedRowFormatted stagedRow) {
        boolean surveyNotDone = stagedRow.getRef().getSpecies().equalsIgnoreCase("Survey Not Done");
        Method method = entityManager.getReference(Method.class, stagedRow.getMethod());
        SurveyMethodEntity surveyMethod = SurveyMethodEntity.builder().survey(survey).method(method).blockNum(stagedRow.getBlock())
                .surveyNotDone(surveyNotDone).build();
        return surveyMethodRepository.save(surveyMethod);
    }

    public List<Observation> getObservations(SurveyMethodEntity surveyMethod, StagedRowFormatted stagedRow,
            Boolean withExtendedSizing) {
        if (!stagedRow.getSpecies().isPresent())
            return Collections.emptyList();

        Diver diver = stagedRow.getDiver();

        Map<Integer, Integer> measures = stagedRow.getMeasureJson();

        Observation.ObservationBuilder baseObservationBuilder = Observation.builder().diver(diver)
                .surveyMethod(surveyMethod).observableItem(stagedRow.getSpecies().get());

        @Value
        class MeasureValue {
            private Integer seqNo;
            private Integer measureValue;
        }

        Stream<MeasureValue> unsized = Stream.empty();

        if (!stagedRow.getRef().getSpecies().equalsIgnoreCase("Survey Not Done") && stagedRow.getInverts() != null && stagedRow.getInverts() > 0) {
            unsized = Stream.of(new MeasureValue(0, stagedRow.getInverts()));
        }

        Stream<MeasureValue> sized = measures.entrySet().stream().map(m -> new MeasureValue(m.getKey(), m.getValue()));

        List<Observation> observations = Stream.concat(unsized, sized).map(m -> {

            Integer method = stagedRow.getMethod();

            int measureTypeId = MeasureType.FishSizeClass;

            if (IntStream.of(SurveyMethod.M0, SurveyMethod.M1, SurveyMethod.M2, SurveyMethod.M7, SurveyMethod.M10, SurveyMethod.M11)
                    .anyMatch(x -> x == method)) {

                if (withExtendedSizing && stagedRow.getIsInvertSizing())
                    measureTypeId =  MeasureType.InvertSizeClass;

                if (stagedRow.getSpecies().get().getObsItemType().getObsItemTypeId() == ObservableItemType.NoSpeciesFound)
                    measureTypeId = MeasureType.Absence;

            } else if (method == SurveyMethod.M3) {
                measureTypeId = MeasureType.InSituQuadrat;
            } else if (method == SurveyMethod.M4) {
                measureTypeId = MeasureType.MacrocystisBlock;
            } else if (method == SurveyMethod.M5) {
                measureTypeId = MeasureType.LimpetQuadrat;
            } else if (method == SurveyMethod.M12) {
                measureTypeId = MeasureType.SingleItem;
            }

            Measure measure = measureRepository.findByMeasureTypeIdAndSeqNo(measureTypeId, m.getSeqNo()).orElse(null);
            return baseObservationBuilder.measure(measure).measureValue(m.getMeasureValue()).build();
        }).collect(Collectors.toList());

        return observations;
    }

    public Map<String, List<StagedRowFormatted>> groupRowsByMethodBlock(List<StagedRowFormatted> surveyRows) {
        return surveyRows.stream().filter(r -> r.getMethodBlock() != null).collect(Collectors.groupingBy(StagedRowFormatted::getMethodBlock));
    }

    @Transactional
    public void ingestTransaction(StagedJob job, Collection<StagedRowFormatted> validatedRows) {

        Map<String, List<StagedRowFormatted>> rowsGroupedBySurvey = validatedRows.stream().collect(Collectors.groupingBy(StagedRowFormatted::getSurvey));
        List<Integer> surveyIds = rowsGroupedBySurvey.values().stream().map(surveyRows -> {
            
            OptionalDouble visAvg = surveyRows.stream().filter(r -> r.getVis().isPresent()).mapToDouble(r -> r.getVis().get()).average();
            if(visAvg.isPresent())
                visAvg = OptionalDouble.of((double)Math.round(visAvg.getAsDouble()));

            Survey survey = getSurvey(job.getProgram(), visAvg, surveyRows.get(0));
            groupRowsByMethodBlock(surveyRows).values().forEach(methodBlockRows -> {
                SurveyMethodEntity surveyMethod = getSurveyMethod(survey, methodBlockRows.get(0));
                methodBlockRows.forEach(row -> observationRepository.saveAll(getObservations(surveyMethod, row, job.getIsExtendedSize())));
            });
            return survey.getSurveyId();
        }).collect(Collectors.toList());

        long rowCount = validatedRows.size();
        long siteCount = validatedRows.stream().map(r -> r.getSite()).distinct().count();
        long surveyCount = surveyIds.size();
        long obsItemCount = validatedRows.stream().map(r -> r.getSpecies()).filter(o -> o.isPresent()).distinct().count();
        long diverCount = validatedRows.stream().map(r -> r.getDiver()).distinct().count();

        List<String> messages = Arrays.asList(rowCount + " rows of data", siteCount + " sites", surveyCount + " surveys", obsItemCount + " distinct observable items", diverCount + " divers");
        String message = messages.stream().collect(Collectors.joining("\n"));

        stagedJobLogRepository.save(StagedJobLog.builder().stagedJob(job).details(message).eventType(StagedJobEventType.INGESTED).build());
        job.setStatus(StatusJobType.INGESTED);
        job.setSurveyIds(surveyIds);
        jobRepository.save(job);
    }
}
