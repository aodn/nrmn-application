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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.Measure;
import au.org.aodn.nrmn.restapi.model.db.Method;
import au.org.aodn.nrmn.restapi.model.db.Observation;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedJobLog;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.SurveyMethod;
import au.org.aodn.nrmn.restapi.model.db.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.MeasureRepository;
import au.org.aodn.nrmn.restapi.repository.MethodRepository;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.ProgramRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyMethodRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import lombok.Value;
import lombok.val;

@Service
public class SurveyIngestionService {

    public static final int METHOD_M0 = 0;
    public static final int METHOD_M1 = 1;
    public static final int METHOD_M2 = 2;
    public static final int METHOD_M3 = 3;
    public static final int METHOD_M4 = 4;
    public static final int METHOD_M5 = 5;
    public static final int METHOD_M7 = 7;
    public static final int METHOD_M10 = 10;
    public static final int METHOD_M11 = 11;
    public static final int METHOD_M12 = 12;

    public static final int MEASURE_TYPE_FISH_SIZE_CLASS = 1;
    public static final int MEASURE_TYPE_IN_SITU_QUADRAT = 2;
    public static final int MEASURE_TYPE_MACROCYSTIS_BLOCK = 3;
    public static final int MEASURE_TYPE_INVERT_SIZE_CLASS = 4;
    public static final int MEASURE_TYPE_SINGLE_ITEM = 5;
    public static final int MEASURE_TYPE_ABSENCE = 6;
    public static final int MEASURE_TYPE_LIMPET_QUADRAT = 7;

    public static final int OBS_ITEM_TYPE_DEBRIS = 5;
    public static final int OBS_ITEM_TYPE_NO_SPECIES_FOUND = 6;

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

        if (!site.getIsActive()) {
            site.setIsActive(true);
            siteRepo.save(site);
        }

        val survey = Survey.builder().depth(stagedRow.getDepth()).surveyNum(stagedRow.getSurveyNum())
                .site(Site.builder().siteCode(site.getSiteCode()).build()).surveyDate(Date.valueOf(stagedRow.getDate()))
                .build();

        Optional<Survey> existingSurvey = surveyRepository.findOne(Example.of(survey));

        return existingSurvey.orElseGet(() -> surveyRepository.save(
                Survey.builder()
                        .depth(stagedRow.getDepth())
                        .surveyNum(stagedRow.getSurveyNum())
                        .direction(stagedRow.getDirection().toString())
                        .site(site).surveyDate(Date.valueOf(stagedRow.getDate()))
                        .surveyTime(Time.valueOf(stagedRow.getTime().orElse(LocalTime.NOON)))
                        .visibility(stagedRow.getVis().orElse(null))
                        .program(stagedRow.getRef().getStagedJob().getProgram())
                        .protectionStatus(site.getProtectionStatus())
                        .insideMarinePark(StringUtils.isNotBlank(site.getMpa()) ? "yes" : "no")
                        .longitude(stagedRow.getLongitude())
                        .latitude(stagedRow.getLatitude())
                        .pqDiverId(stagedRow.getPqs() != null ? stagedRow.getPqs().getDiverId() : null)
                        .build()));
    }

    public SurveyMethod getSurveyMethod(Survey survey, StagedRowFormatted stagedRow) {
        boolean surveyNotDone = stagedRow.getCode().toLowerCase().equals("snd");
        Method method = entityManager.getReference(Method.class, stagedRow.getMethod());
        val surveyMethod = SurveyMethod.builder().survey(survey).method(method).blockNum(stagedRow.getBlock())
                .surveyNotDone(surveyNotDone).build();
        return surveyMethodRepository.save(surveyMethod);
    }

    public List<Observation> getObservations(SurveyMethod surveyMethod, StagedRowFormatted stagedRow,
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

        if (!stagedRow.getCode().equalsIgnoreCase("snd") && stagedRow.getInverts() > 0) {
            unsized = Stream.of(new MeasureValue(0, stagedRow.getInverts()));
        }

        Stream<MeasureValue> sized = measures.entrySet().stream().map(m -> new MeasureValue(m.getKey(), m.getValue()));

        List<Observation> observations = Stream.concat(unsized, sized).map(m -> {

            Integer method = stagedRow.getMethod();

            int measureTypeId = MEASURE_TYPE_FISH_SIZE_CLASS;

            if (IntStream.of(METHOD_M0, METHOD_M1, METHOD_M2, METHOD_M7, METHOD_M10, METHOD_M11)
                    .anyMatch(x -> x == method)) {

                if (withExtendedSizing) {
                    measureTypeId = stagedRow.getIsInvertSizing() ? MEASURE_TYPE_INVERT_SIZE_CLASS
                            : MEASURE_TYPE_FISH_SIZE_CLASS;
                }

                if (stagedRow.getSpecies().get().getObsItemType().getObsItemTypeId() == OBS_ITEM_TYPE_NO_SPECIES_FOUND)
                    measureTypeId = MEASURE_TYPE_ABSENCE;

            } else if (method == METHOD_M3) {
                measureTypeId = MEASURE_TYPE_IN_SITU_QUADRAT;
            } else if (method == METHOD_M4) {
                measureTypeId = MEASURE_TYPE_MACROCYSTIS_BLOCK;
            } else if (method == METHOD_M5) {
                measureTypeId = MEASURE_TYPE_LIMPET_QUADRAT;
            } else if (method == METHOD_M12) {
                measureTypeId = MEASURE_TYPE_SINGLE_ITEM;
            }

            Measure measure = measureRepository.findByMeasureTypeIdAndSeqNo(measureTypeId, m.getSeqNo()).orElse(null);
            return baseObservationBuilder.measure(measure).measureValue(m.getMeasureValue()).build();
        }).collect(Collectors.toList());

        return observations;
    }

    public Map<String, List<StagedRowFormatted>> groupRowsBySurveyMethod(List<StagedRowFormatted> surveyRows) {
        return surveyRows.stream().map(r -> {
            if (r.getSpecies().isPresent() && r.getSpecies().get().getObsItemType().getObsItemTypeId() == OBS_ITEM_TYPE_DEBRIS)
                r.setMethod(METHOD_M12);
            return r;
        }).collect(Collectors.groupingBy(StagedRowFormatted::getSurveyName));
    }

    @Transactional
    public void ingestTransaction(StagedJob job, Collection<StagedRowFormatted> validatedRows) {

        Map<String, List<StagedRowFormatted>> rowsGroupedBySurvey = validatedRows.stream().collect(Collectors.groupingBy(StagedRowFormatted::getTransectName));

        List<Integer> surveyIds = rowsGroupedBySurvey.values().stream().map(surveyRows -> {
            Survey survey = getSurvey(surveyRows.get(0));
            groupRowsBySurveyMethod(surveyRows).values().forEach(surveyMethodRows -> {
                SurveyMethod surveyMethod = getSurveyMethod(survey, surveyMethodRows.get(0));
                surveyMethodRows.forEach(row -> observationRepository
                        .saveAll(getObservations(surveyMethod, row, job.getIsExtendedSize())));
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

        stagedJobLogRepository.save(StagedJobLog.builder().stagedJob(job).details(message).eventType(StagedJobEventType.INGESTING).build());
        job.setStatus(StatusJobType.INGESTED);
        job.setSurveyIds(surveyIds);
        jobRepository.save(job);
    }
}
