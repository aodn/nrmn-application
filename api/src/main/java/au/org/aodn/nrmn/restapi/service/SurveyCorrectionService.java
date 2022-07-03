package au.org.aodn.nrmn.restapi.service;

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
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.SurveyMethodEntity;
import au.org.aodn.nrmn.restapi.model.db.enums.MeasureType;
import au.org.aodn.nrmn.restapi.model.db.enums.ObservableItemType;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
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
public class SurveyCorrectionService {

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
                    measureTypeId = MeasureType.InvertSizeClass;

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
            
            Observation observation = Observation.builder().diver(diver).surveyMethod(surveyMethod).observableItem(stagedRow.getSpecies().get()).measure(measure).measureValue(m.getMeasureValue()).build();

            return observation;
        }).collect(Collectors.toList());

        return observations;
    }

    public Map<String, List<StagedRowFormatted>> groupRowsByMethodBlock(List<StagedRowFormatted> surveyRows) {
        return surveyRows.stream().filter(r -> r.getMethodBlock() != null).collect(Collectors.groupingBy(StagedRowFormatted::getMethodBlock));
    }

    @Transactional
    public void correctionTransaction(Survey survey, Collection<Observation> observations) {
        var surveyId = survey.getSurveyId();
        observationRepository.deleteAllForSurvey(surveyId);
        surveyMethodRepository.deleteForSurveyId(surveyId);
        surveyRepository.save(survey);
    }
}
