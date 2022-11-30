package au.org.aodn.nrmn.restapi.model.db;

import java.util.HashMap;
import java.util.Map;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.Diver;
import au.org.aodn.nrmn.restapi.data.model.Observation;
import au.org.aodn.nrmn.restapi.data.model.SurveyMethodEntity;
import au.org.aodn.nrmn.restapi.data.model.Observation.ObservationBuilder;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;

@Component
public class ObservationTestData {

    @Autowired
    private DiverTestData diverTestData;

    @Autowired
    private ObservableItemTestData observableItemTestData;

    @Autowired
    private MeasureTestData measureTestData;

    @Autowired
    private ObservationRepository observationRepository;

    public Observation persistedObservation(Observation observation) {
        observationRepository.saveAndFlush(observation);
        return observation;
    }


    public Observation buildWith(SurveyMethodEntity enitity, Diver diver, int itemNumber) {
        return buildWith(enitity, diver, null, itemNumber);
    }

    public Observation buildWith(SurveyMethodEntity enitity, Diver diver, ObservableItem oi, int itemNumber) {
        final Map<String, String> observationAttribute = new HashMap<>();
        observationAttribute.put("Item Number", String.valueOf(itemNumber));
        return Observation.builder()
                .surveyMethod(enitity)
                .diver(diver)
                .observableItem(observableItemTestData.persistedObservableItem(oi == null ? observableItemTestData.defaultBuilder().build() : oi))
                .measure(measureTestData.persistedMeasure())
                .measureValue(itemNumber)
                .observationAttribute(observationAttribute)
                .build();

    }

    public ObservationBuilder defaultBuilder() {
        final Map<String, String> observationAttribute = new HashMap<>();
        observationAttribute.put("Biomass", "0.7630353218");
        return Observation.builder()
            .diver(diverTestData.persistedDiver())
            .observableItem(observableItemTestData.persistedObservableItem(observableItemTestData.defaultBuilder().build()))
            .measure(measureTestData.persistedMeasure())
            .measureValue(4)
            .observationAttribute(observationAttribute);
    }

}
