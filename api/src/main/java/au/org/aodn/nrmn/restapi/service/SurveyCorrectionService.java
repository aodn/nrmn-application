package au.org.aodn.nrmn.restapi.service;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
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
    ObservationRepository observationRepository;

    @Autowired
    StagedJobLogRepository stagedJobLogRepository;

    @Autowired
    StagedJobRepository jobRepository;

    private static Logger logger = LoggerFactory.getLogger(SurveyCorrectionService.class);

    private List<Integer> speciesMethods = Arrays.asList(
            SurveyMethod.M0,
            SurveyMethod.M1,
            SurveyMethod.M2,
            SurveyMethod.M7,
            SurveyMethod.M10,
            SurveyMethod.M11);

    public void correctSurvey(StagedJob job, List<StagedJobLog> jobLogs, List<Integer> surveyIds, Collection<StagedRowFormatted> validatedRows) {
        surveyCorrectionTransaction(job, jobLogs, surveyIds, validatedRows);
    }

    public void correctSpecies(StagedJob job, List<Integer> surveyIds, ObservableItem curr, ObservableItem next) {
        speciesCorrectionTransaction(job, surveyIds, curr, next);
    }

    public void deleteSurvey(StagedJob job, Survey survey, Collection<StagedRowFormatted> validatedRows) {
        surveyDeletionTransaction(job, survey, validatedRows);
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

    private void surveyDeletionTransaction(StagedJob job, Survey survey, Collection<StagedRowFormatted> validatedRows) {
        var messages = new ArrayList<String>();

        if (survey.getLocked() != null && survey.getLocked())
            throw new RuntimeException("Cannot delete a survey with locked data");

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

    private void speciesCorrectionTransaction(StagedJob job, List<Integer> surveyIds, ObservableItem curr,
            ObservableItem next) {

        var messages = new ArrayList<String>();

        observationRepository.updateObservableItemsForSurveys(surveyIds, curr.getObservableItemId(),
                next.getObservableItemId());

        messages.add("Correcting species from " + curr.getObservableItemName() + " to " + next.getObservableItemName());

        var details = messages.stream().collect(Collectors.joining("\n"));
        var log = StagedJobLog.builder().stagedJob(job).details(details).eventType(StagedJobEventType.CORRECTED);
        stagedJobLogRepository.save(log.build());
        job.setStatus(StatusJobType.CORRECTED);
        job.setSurveyIds(surveyIds);
        jobRepository.save(job);
    }

    private void surveyCorrectionTransaction(StagedJob job,
            List<StagedJobLog> jobLogs,
            List<Integer> surveyIds,
            Collection<StagedRowFormatted> validatedRows) {

        var messages = new ArrayList<String>();

        var rowsGroupedBySurvey = validatedRows.stream()
                .collect(Collectors.groupingBy(StagedRowFormatted::getSurveyId));

        // ASSERT: that the survey ids in the grouped rows match the list of survey ids
        var groupedSurveyIds = rowsGroupedBySurvey.keySet().stream().map(l -> l.intValue())
                .collect(Collectors.toList());

        if (!surveyIds.containsAll(groupedSurveyIds))
            throw new RuntimeException("Correction would delete one or more surveys.");

        if (!groupedSurveyIds.containsAll(surveyIds))
            throw new RuntimeException("Correction would create one or more surveys.");

        // ASSERT: locked surveys are not affected
        var surveys = surveyRepository.findAllById(surveyIds);
        if (surveys.stream().anyMatch(s -> s.getLocked() != null && s.getLocked()))
            throw new RuntimeException("Cannot correct locked survey data");

        surveyMethodRepository.deleteForSurveyIds(surveyIds);

        var surveyMethods = new ArrayList<SurveyMethodEntity>();
        var observations = new ArrayList<Observation>();
        var methodEntities = methodRepository.findAll();
        var measureEntities = measureRepository.findAll();

        for (var survey : surveys) {
            logger.info("survey-correction", "Correcting Survey ID: " + survey.getSurveyId());

            var surveyRows = rowsGroupedBySurvey.get(survey.getSurveyId().longValue());

            var firstSurveyRow = surveyRows.get(0);
            survey.setSurveyNum(firstSurveyRow.getSurveyNum());
            var direction = firstSurveyRow.getDirection() != null ? firstSurveyRow.getDirection().toString() : null;
            survey.setDirection(direction);
            survey.setSurveyTime(Time.valueOf(firstSurveyRow.getTime().orElse(LocalTime.NOON)));
            survey.setLongitude(firstSurveyRow.getLongitude());
            survey.setLatitude(firstSurveyRow.getLatitude());
            survey.setPqDiverId(firstSurveyRow.getPqs() != null ? firstSurveyRow.getPqs().getDiverId() : null);

            var visAvg = surveyRows.stream()
                    .filter(r -> r.getVis().isPresent())
                    .mapToDouble(r -> r.getVis().get())
                    .average();

            if (visAvg.isPresent())
                visAvg = OptionalDouble.of((double) Math.round(visAvg.getAsDouble()));

            survey.setVisibility(visAvg.orElse(0.0));

            var surveyMethodBlocks = surveyRows.stream().filter(r -> r.getMethodBlock() != null)
                    .collect(Collectors.groupingBy(StagedRowFormatted::getMethodBlock));

            for (var methodBlockRows : surveyMethodBlocks.values()) {

                var firstMethodBlockRow = methodBlockRows.get(0);
                var surveyNotDone = firstMethodBlockRow.getRef().getSpecies().equalsIgnoreCase("Survey Not Done");
                var method = methodEntities.stream().filter(m -> m.getMethodId() == firstMethodBlockRow.getMethod())
                        .findFirst()
                        .orElse(null);
                var surveyMethod = SurveyMethodEntity.builder().survey(survey).method(method)
                        .blockNum(firstMethodBlockRow.getBlock())
                        .surveyNotDone(surveyNotDone).build();

                surveyMethods.add(surveyMethod);

                for (var row : methodBlockRows) {

                    var diver = row.getDiver();

                    var measures = row.getMeasureJson();

                    if (!surveyNotDone && row.getInverts() != null && row.getInverts() > 0)
                        measures.put(0, row.getInverts());

                    for (var m : measures.entrySet()) {

                        var measureTypeId = MeasureType.fromMethodId(method.getMethodId());

                        if (speciesMethods.contains(method.getMethodId())) {

                            if (row.getIsInvertSizing())
                                measureTypeId = MeasureType.InvertSizeClass;

                            var obsItemType = row.getSpecies().get().getObsItemType();
                            if (obsItemType.getObsItemTypeId() == ObservableItemType.NoSpeciesFound)
                                measureTypeId = MeasureType.Absence;

                        }

                        final var measureTypeId_ = measureTypeId;
                        var measure = measureEntities.stream()
                                .filter(me -> me.getMeasureType().getMeasureTypeId() == measureTypeId_
                                        && me.getSeqNo() == m.getKey())
                                .findFirst().orElse(null);

                        var observation = Observation.builder()
                                .diver(diver)
                                .surveyMethod(surveyMethod)
                                .observableItem(row.getSpecies().get())
                                .measure(measure)
                                .measureValue(m.getValue())
                                .build();

                        observations.add(observation);
                    }
                }

                logger.info("survey-correction",
                        "Correcting " + firstMethodBlockRow.getBlock() + " : " + method.getMethodId());
            }
        }

        surveyRepository.saveAll(surveys);
        surveyMethodRepository.saveAll(surveyMethods);
        var newObservations = observationRepository.saveAll(observations);

        logger.info("survey-correction", "Inserting Observations "
                + formatRange(newObservations.stream().map(o -> o.getObservationId()).collect(Collectors.toList())));

        var details = messages.stream().collect(Collectors.joining("\n"));
        var log = StagedJobLog.builder().stagedJob(job).details(details).eventType(StagedJobEventType.CORRECTED);
        stagedJobLogRepository.save(log.build());
        job.setStatus(StatusJobType.CORRECTED);
        job.setSurveyIds(surveyIds);
        jobRepository.save(job);
        surveyRepository.updateSurveyModified(surveyIds);
    }
}
