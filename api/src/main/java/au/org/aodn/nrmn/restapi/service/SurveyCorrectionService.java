package au.org.aodn.nrmn.restapi.service;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.tuple.Pair;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.Observation;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedJobLog;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.model.Survey;
import au.org.aodn.nrmn.restapi.data.model.SurveyMethodEntity;
import au.org.aodn.nrmn.restapi.data.repository.CorrectionRowRepository;
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
import au.org.aodn.nrmn.restapi.dto.correction.CorrectionDiffCellDto;
import au.org.aodn.nrmn.restapi.dto.correction.CorrectionDiffDto;
import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRowDto;
import au.org.aodn.nrmn.restapi.enums.MeasureType;
import au.org.aodn.nrmn.restapi.enums.ObservableItemType;
import au.org.aodn.nrmn.restapi.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.enums.SurveyMethod;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.util.ObjectUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

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
    CorrectionRowRepository correctionRowRepository;

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

    public CorrectionDiffDto diffSurveyCorrections(List<Integer> surveyIds, Collection<StagedRow> correctedRows) {

        var ingestedRows = correctionRowRepository.findRowsBySurveyIds(surveyIds);

        var ingestedRowIds = ingestedRows.stream().map(CorrectionRowDto::getDiffRowId).collect(Collectors.toList());

        var correctdRowIds = correctedRows.stream().map(StagedRow::getDiffRowId).collect(Collectors.toList());

        var deletedRowIds = ingestedRowIds.stream().filter(i -> !correctdRowIds.contains(i))
                .collect(Collectors.toList());

        var insertedRowIds = correctdRowIds.stream().filter(i -> !ingestedRowIds.contains(i))
                .collect(Collectors.toList());

        var updatedRowsIds = correctdRowIds.stream().filter(i -> ingestedRowIds.contains(i))
                .collect(Collectors.toList());

        var cellDiffs = new ArrayList<CorrectionDiffCellDto>();

        var propertyChecks = new HashMap<String, Pair<Function<CorrectionRowDto, String>, Function<StagedRow, String>>>() {
            {
                put("diver", Pair.of(CorrectionRowDto::getDiver, StagedRow::getDiver));
                put("latitude", Pair.of(CorrectionRowDto::getLatitude, StagedRow::getLatitude));
                put("longitude", Pair.of(CorrectionRowDto::getLongitude, StagedRow::getLongitude));
                put("direction", Pair.of(CorrectionRowDto::getDirection, StagedRow::getDirection));
                put("vis", Pair.of(CorrectionRowDto::getVis, StagedRow::getVis));
                put("time", Pair.of(CorrectionRowDto::getTime, StagedRow::getTime));
                put("pqDiver", Pair.of(CorrectionRowDto::getPqDiver, StagedRow::getPqs));
                put("method", Pair.of(CorrectionRowDto::getMethod, StagedRow::getMethod));
                put("block", Pair.of(CorrectionRowDto::getBlock, StagedRow::getBlock));
                put("code", Pair.of(CorrectionRowDto::getCode, StagedRow::getCode));
                put("species", Pair.of(CorrectionRowDto::getSpecies, StagedRow::getSpecies));
                put("commonName", Pair.of(CorrectionRowDto::getCommonName, StagedRow::getCommonName));
                put("total", Pair.of(CorrectionRowDto::getTotal, StagedRow::getTotal));
                put("isInvertSizing", Pair.of(CorrectionRowDto::getIsInvertSizing, StagedRow::getIsInvertSizing));
            }
        };

        var objectMapper = new ObjectMapper();
        var measurementType = new TypeReference<Map<Integer, String>>() {
        };

        for (var id : updatedRowsIds) {

            var aOptional = ingestedRows.stream()
                    .filter(r -> Objects.nonNull(r.getDiffRowId()))
                    .filter(r -> r.getDiffRowId().contentEquals(id))
                    .findFirst();

            var bOptional = correctedRows.stream()
                    .filter(r -> Objects.nonNull(r.getDiffRowId()))
                    .filter(r -> r.getDiffRowId().contentEquals(id))
                    .findFirst();

            if (aOptional.isPresent() && bOptional.isPresent()) {

                var a = aOptional.get();
                var b = bOptional.get();

                for (var entry : propertyChecks.entrySet()) {
                    var getterA = entry.getValue().getLeft();
                    var getterB = entry.getValue().getRight();
                    if (ObjectUtils.stringPropertiesDiffer(true, getterA, getterB, a, b))
                        cellDiffs.add(CorrectionDiffCellDto.builder()
                                .columnName(entry.getKey())
                                .diffRowId(id)
                                .oldValue(getterA.apply(a))
                                .newValue(getterB.apply(b))
                                .build());
                }

                try {
                    var measureA = objectMapper.readValue(a.getMeasureJson(), measurementType);
                    var measureB = b.getMeasureJson();

                    // Put inverts into the measurement map for the StagedRow entity.
                    // StagedRow stores inverts in a column but CorrectionRowDto uses key 0.
                    if (!b.getInverts().equals("0"))
                        measureB.put(0, b.getInverts());

                    var measureDiff = Maps.difference(measureA, measureB);

                    for (var diff : measureDiff.entriesOnlyOnLeft().entrySet()) {
                        cellDiffs.add(CorrectionDiffCellDto.builder()
                                .columnName(diff.getKey().toString())
                                .diffRowId(id)
                                .oldValue(diff.getValue())
                                .newValue("0")
                                .build());
                    }

                    for (var diff : measureDiff.entriesOnlyOnRight().entrySet()) {
                        cellDiffs.add(CorrectionDiffCellDto.builder()
                                .columnName(diff.getKey().toString())
                                .diffRowId(id)
                                .oldValue("0")
                                .newValue(diff.getValue())
                                .build());
                    }

                    for (var diff : measureDiff.entriesDiffering().entrySet()) {
                        var lv = diff.getValue().leftValue();
                        var rv = diff.getValue().rightValue();
                        cellDiffs.add(CorrectionDiffCellDto.builder()
                                .columnName(diff.getKey().toString())
                                .diffRowId(id)
                                .oldValue(lv)
                                .newValue(rv)
                                .build());
                    }
                } catch (Exception ex) {
                    logger.error("Failed to measurement diff", ex.getMessage());
                }

            }

        }

        var diff = new CorrectionDiffDto();
        diff.setDeletedRows(deletedRowIds);
        diff.setInsertedRows(insertedRowIds);
        diff.setCellDiffs(cellDiffs);
        return diff;
    }

    public void correctSurvey(StagedJob job, List<Integer> surveyIds, Collection<StagedRowFormatted> validatedRows) {
        surveyCorrectionTransaction(job, surveyIds, validatedRows);
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

    private void surveyCorrectionTransaction(StagedJob job, List<Integer> surveyIds,
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
            messages.add("Correcting Survey ID: " + survey.getSurveyId());

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
                messages.add("Correcting " + firstMethodBlockRow.getBlock() + " : " + method.getMethodId());
            }
        }

        surveyRepository.saveAll(surveys);
        surveyMethodRepository.saveAll(surveyMethods);
        var newObservations = observationRepository.saveAll(observations);

        messages.add("Inserting Observations "
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
