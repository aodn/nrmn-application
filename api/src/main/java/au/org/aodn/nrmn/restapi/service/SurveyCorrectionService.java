package au.org.aodn.nrmn.restapi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.data.model.Method;
import au.org.aodn.nrmn.restapi.data.model.Observation;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedJobLog;
import au.org.aodn.nrmn.restapi.data.model.Survey;
import au.org.aodn.nrmn.restapi.data.model.SurveyMethodEntity;
import au.org.aodn.nrmn.restapi.data.repository.MeasureRepository;
import au.org.aodn.nrmn.restapi.data.repository.MethodRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.ProgramRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyMethodRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.enums.MeasureType;
import au.org.aodn.nrmn.restapi.enums.ObservableItemType;
import au.org.aodn.nrmn.restapi.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.enums.SurveyMethod;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;

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

    private List<Integer> speciesMethods = Arrays.asList(
            SurveyMethod.M0,
            SurveyMethod.M1,
            SurveyMethod.M2,
            SurveyMethod.M7,
            SurveyMethod.M10,
            SurveyMethod.M11);

    public void correctSurvey(StagedJob job, List<Integer> surveyIds, Collection<StagedRowFormatted> validatedRows) {
        correctionTransaction(job, surveyIds, validatedRows);
    }

    public void deleteSurvey(StagedJob job, Survey survey, Collection<StagedRowFormatted> validatedRows) {
        deletionTransaction(job, survey, validatedRows);
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

    private void correctionTransaction(StagedJob job, List<Integer> surveyIds,
            Collection<StagedRowFormatted> validatedRows) {

        var messages = new ArrayList<String>();

        var rowsGroupedBySurvey = validatedRows.stream()
                .collect(Collectors.groupingBy(StagedRowFormatted::getSurveyId));

        // ASSERT: that the survey ids in the grouped rows match the list of survey ids
        var groupedSurveyIds = rowsGroupedBySurvey.keySet().stream().map(l -> l.intValue())
                .collect(Collectors.toList());

        if (!surveyIds.containsAll(groupedSurveyIds) || !groupedSurveyIds.containsAll(surveyIds)) {
            throw new RuntimeException("Survey IDs in grouped rows do not match the list of survey IDs passed in");
        }

        for (var surveyId : surveyIds) {
            messages.add("Delete Observation IDs: " + observationsForSurveySummary(surveyId));

            var survey = surveyRepository.findById(surveyId).orElse(null);
            surveyMethodRepository.deleteForSurveyId(surveyId);
            var surveyRows = rowsGroupedBySurvey.get(surveyId.longValue());

            var visAvg = surveyRows.stream().filter(r -> r.getVis().isPresent()).mapToDouble(r -> r.getVis().get()).average();
            if(visAvg.isPresent())
                visAvg = OptionalDouble.of((double)Math.round(visAvg.getAsDouble()));

            var newIds = new ArrayList<Integer>();
            var surveyMethodBlocks = surveyRows.stream().filter(r -> r.getMethodBlock() != null)
                    .collect(Collectors.groupingBy(StagedRowFormatted::getMethodBlock));

            for(var methodBlockRows : surveyMethodBlocks.values()) {

                var firstRow = methodBlockRows.get(0);
                var surveyNotDone = firstRow.getRef().getSpecies().equalsIgnoreCase("Survey Not Done");
                var method = entityManager.getReference(Method.class, firstRow.getMethod());
                var surveyMethod = SurveyMethodEntity.builder().survey(survey).method(method)
                        .blockNum(firstRow.getBlock())
                        .surveyNotDone(surveyNotDone).build();

                // TODO: move out of the loop if we can
                surveyMethod = surveyMethodRepository.save(surveyMethod);

                for (var row : methodBlockRows) {

                    var diver = row.getDiver();

                    var measures = row.getMeasureJson();

                    if (!surveyNotDone && row.getInverts() != null && row.getInverts() > 0)
                        measures.put(0, row.getInverts());

                    var observations = new ArrayList<Observation>();

                    for (var m : measures.entrySet()) {

                        var measureTypeId = MeasureType.FishSizeClass;

                        switch (method.getMethodId()) {
                            case SurveyMethod.M3:
                                measureTypeId = MeasureType.InSituQuadrat;
                                break;
                            case SurveyMethod.M4:
                                measureTypeId = MeasureType.MacrocystisBlock;
                                break;
                            case SurveyMethod.M5:
                                measureTypeId = MeasureType.LimpetQuadrat;
                                break;
                            case SurveyMethod.M12:
                                measureTypeId = MeasureType.SingleItem;
                                break;
                            default:
                                measureTypeId = MeasureType.FishSizeClass;
                        }

                        if (speciesMethods.contains(method.getMethodId())) {

                            if (job.getIsExtendedSize() && row.getIsInvertSizing())
                                measureTypeId = MeasureType.InvertSizeClass;

                            var obsItemType = row.getSpecies().get().getObsItemType();
                            if (obsItemType.getObsItemTypeId() == ObservableItemType.NoSpeciesFound)
                                measureTypeId = MeasureType.Absence;

                        }

                        var measure = measureRepository.findByMeasureTypeIdAndSeqNo(measureTypeId, m.getKey())
                                .orElse(null);

                        var observation = Observation.builder()
                                .diver(diver)
                                .surveyMethod(surveyMethod)
                                .observableItem(row.getSpecies().get())
                                .measure(measure)
                                .measureValue(m.getValue())
                                .build();

                        observations.add(observation);
                    }

                    var newObservations = observationRepository.saveAll(observations);
                    newIds.addAll(newObservations.stream().map(o -> o.getObservationId()).collect(Collectors.toList()));
                }

                messages.add("Insert Observation IDs: " + formatRange(newIds));
                messages.add("Update Survey ID: " + surveyId);
            }
        }

        //TODO: set survey last updated and save new vis avg
        var details = messages.stream().collect(Collectors.joining("\n"));
        var log = StagedJobLog.builder().stagedJob(job).details(details).eventType(StagedJobEventType.CORRECTED);
        stagedJobLogRepository.save(log.build());
        job.setStatus(StatusJobType.CORRECTED);
        job.setSurveyIds(surveyIds);
        jobRepository.save(job);
        surveyRepository.updateSurveyModified();
    }
}
