package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CorrectionService {
    @Autowired
    private ObservationRepository observationRepository;
    @Autowired
    private SurveyMethodRepository surveyMethodRepository;

    public Stream<StagedRow> convertSurveyToStagedRows(Survey survey) {
        List<SurveyMethod> surveyMethods = surveyMethodRepository.findAll(Example.of(
                SurveyMethod.builder().survey(survey).build()));
        return surveyMethods.stream().map(this::convertSurveyMethodToStagedRow);
    }

    private StagedRow convertSurveyMethodToStagedRow(SurveyMethod surveyMethod) {
        Survey survey = surveyMethod.getSurvey();
        StagedRow.StagedRowBuilder builder = StagedRow.builder()
                .depth(getStagedDepth(survey.getDepth(), survey.getSurveyNum()))
                .siteCode(survey.getSite().getSiteCode())
                .siteName(survey.getSite().getSiteName())
                .date(getDateString(survey.getSurveyDate()))
                .time(getTimeString(survey.getSurveyTime()))
                .vis(toString(survey.getVisibility()))
                .direction(survey.getDirection())
                .block(toString(surveyMethod.getBlockNum()))
                .method(toString(surveyMethod.getMethod().getMethodId()))
                .code(surveyMethod.getSurveyNotDone() ? "SND" : null);
        List<Observation> observations = observationRepository.findAll(Example.of(
                Observation.builder().surveyMethod(surveyMethod).build()));

        observations.stream()
                .filter(obs -> obs.getMeasure().getSeqNo() == 0).findAny()
                .ifPresent(obs -> builder.inverts(obs.getMeasureValue().toString()));

        observations.stream().findAny().ifPresent(obs -> builder
                .species(getSpeciesScientificName(obs.getObservableItem()))
                .diver(toString(obs.getDiver().getDiverId()))
        );

        builder.measureJson(observations.stream()
                .collect(Collectors.toMap(
                        obs -> obs.getMeasure().getSeqNo(),
                        obs -> obs.getMeasureValue().toString())));

        return builder.build();
    }


    private String toString(Object object) {
        return object == null ? null : object.toString();
    }

    private String getDateString(Date date) {
        return date.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String getTimeString(Time time) {
        if (time == null) {
            return null;
        } else {
            return time.toString();
        }
    }

    private String getStagedDepth(int depth, int surveyNum) {
        return String.format("{}.{}", depth, surveyNum);
    }

    private String getSpeciesScientificName(ObservableItem observableItem) {
        return observableItem.getObservableItemName();
    }

}
