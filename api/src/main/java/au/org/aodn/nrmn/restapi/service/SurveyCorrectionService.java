package au.org.aodn.nrmn.restapi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.Measure;
import au.org.aodn.nrmn.restapi.model.db.Method;
import au.org.aodn.nrmn.restapi.model.db.Observation;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedJobLog;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.SurveyMethodEntity;
import au.org.aodn.nrmn.restapi.model.db.enums.MeasureType;
import au.org.aodn.nrmn.restapi.model.db.enums.ObservableItemType;
import au.org.aodn.nrmn.restapi.model.db.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.SurveyMethod;
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

@Service
@Transactional
public class SurveyCorrectionService {

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    MethodRepository methodRepository;

    @Autowired
    MeasureRepository measureRepository;

    @Autowired
    SurveyMethodRepository surveyMethodRepository;

    @Autowired
    ObservableItemRepository observableItemRepository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ObservationRepository observationRepository;

    @Autowired
    StagedJobLogRepository stagedJobLogRepository;

    @Autowired
    StagedJobRepository jobRepository;

    public void correctSurvey(StagedJob job, Survey survey, Collection<StagedRowFormatted> validatedRows) {
        correctionTransaction(job, survey, validatedRows);
    }

    public void deleteSurvey(StagedJob job, Survey survey, Collection<StagedRowFormatted> validatedRows) {
        deletionTransaction(job, survey, validatedRows);
    }

    private SurveyMethodEntity getSurveyMethod(Survey survey, StagedRowFormatted stagedRow) {
        var surveyNotDone = stagedRow.getRef().getSpecies().equalsIgnoreCase("Survey Not Done");
        var method = entityManager.getReference(Method.class, stagedRow.getMethod());
        var surveyMethod = SurveyMethodEntity.builder().survey(survey).method(method)
                .blockNum(stagedRow.getBlock())
                .surveyNotDone(surveyNotDone).build();
        return surveyMethodRepository.save(surveyMethod);
    }

    private List<Observation> getObservations(SurveyMethodEntity surveyMethod, StagedRowFormatted stagedRow,
            Boolean withExtendedSizing) {
        if (!stagedRow.getSpecies().isPresent())
            return Collections.emptyList();

        Diver diver = stagedRow.getDiver();

        Map<Integer, Integer> measures = stagedRow.getMeasureJson();

        @Value
        class MeasureValue {
            private Integer seqNo;
            private Integer measureValue;
        }

        Stream<MeasureValue> unsized = Stream.empty();

        if (!stagedRow.getRef().getSpecies().equalsIgnoreCase("Survey Not Done") && stagedRow.getInverts() != null
                && stagedRow.getInverts() > 0) {
            unsized = Stream.of(new MeasureValue(0, stagedRow.getInverts()));
        }

        Stream<MeasureValue> sized = measures.entrySet().stream().map(m -> new MeasureValue(m.getKey(), m.getValue()));

        List<Observation> observations = Stream.concat(unsized, sized).map(m -> {

            Integer method = stagedRow.getMethod();

            int measureTypeId = MeasureType.FishSizeClass;

            if (IntStream
                    .of(SurveyMethod.M0, SurveyMethod.M1, SurveyMethod.M2, SurveyMethod.M7, SurveyMethod.M10,
                            SurveyMethod.M11)
                    .anyMatch(x -> x == method)) {

                if (withExtendedSizing && stagedRow.getIsInvertSizing())
                    measureTypeId = MeasureType.InvertSizeClass;

                if (stagedRow.getSpecies().get().getObsItemType()
                        .getObsItemTypeId() == ObservableItemType.NoSpeciesFound)
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

            Observation observation = Observation.builder().diver(diver).surveyMethod(surveyMethod)
                    .observableItem(stagedRow.getSpecies().get()).measure(measure).measureValue(m.getMeasureValue())
                    .build();

            return observation;
        }).collect(Collectors.toList());

        return observations;
    }

    private Map<String, List<StagedRowFormatted>> groupRowsByMethodBlock(List<StagedRowFormatted> surveyRows) {
        return surveyRows.stream().filter(r -> r.getMethodBlock() != null)
                .collect(Collectors.groupingBy(StagedRowFormatted::getMethodBlock));
    }

    private String formatRange(List<Integer> ids) {
        var min = ids.get(0);
        var max = ids.get(ids.size() - 1);
        var range = IntStream.rangeClosed(min, max).boxed().collect(Collectors.toList());

        return (ids.equals(range))
                ? "[" + min + " to " + max + "]"
                : ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    private String observationsForSurveySummary(Integer surveyId) {
        return formatRange(observationRepository.findObservationIdsForSurvey(surveyId));
    }

    private void deletionTransaction(StagedJob job, Survey survey, Collection<StagedRowFormatted> validatedRows) {
        var messages = new ArrayList<String>();

        // Remove existing observations
        var surveyId = survey.getSurveyId();
        messages.add("Delete Observation IDs: " + observationsForSurveySummary(surveyId));
        surveyMethodRepository.deleteForSurveyId(surveyId);

        surveyRepository.deleteById(survey.getSurveyId());
        messages.add("Delete Survey ID: " + surveyId);

        stagedJobLogRepository.save(
                StagedJobLog.builder()
                        .stagedJob(job)
                        .details(messages.stream().collect(Collectors.joining("\n")))
                        .eventType(StagedJobEventType.CORRECTED)
                        .build());
        job.setStatus(StatusJobType.CORRECTED);
        jobRepository.save(job);
    }

    private void correctionTransaction(StagedJob job, Survey survey, Collection<StagedRowFormatted> validatedRows) {

        var surveyIds = Arrays.<Integer>asList();
        var messages = new ArrayList<String>();

        var surveyId = survey.getSurveyId();
        messages.add("Delete Observation IDs: " + observationsForSurveySummary(surveyId));
        surveyMethodRepository.deleteForSurveyId(surveyId);

        var rowsGroupedBySurvey = validatedRows.stream().collect(Collectors.groupingBy(StagedRowFormatted::getSurvey));

        var newIds = new ArrayList<Integer>();
        surveyIds = rowsGroupedBySurvey.values().stream().map(surveyRows -> {
            groupRowsByMethodBlock(surveyRows).values().forEach(methodBlockRows -> {
                var surveyMethod = getSurveyMethod(survey, methodBlockRows.get(0));
                methodBlockRows.forEach(row -> {
                    var newObservations = observationRepository.saveAll(
                            getObservations(surveyMethod, row, job.getIsExtendedSize()));
                    newIds.addAll(newObservations.stream().map(o -> o.getObservationId()).collect(Collectors.toList()));
                });
            });
            messages.add("Insert Observation IDs: " + formatRange(newIds));
            messages.add("Update Survey ID: " + surveyId);
            return surveyId;
        }).collect(Collectors.toList());

        stagedJobLogRepository.save(
                StagedJobLog.builder()
                        .stagedJob(job)
                        .details(messages.stream().collect(Collectors.joining("\n")))
                        .eventType(StagedJobEventType.CORRECTED)
                        .build());
        job.setStatus(StatusJobType.CORRECTED);
        job.setSurveyIds(surveyIds);
        jobRepository.save(job);
        surveyRepository.updateSurveyModified();
    }
}
