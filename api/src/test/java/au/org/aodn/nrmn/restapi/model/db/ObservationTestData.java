package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.Observation.ObservationBuilder;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObservationTestData {

    @Autowired
    private DiverTestData diverTestData;

    @Autowired
    private ObservableItemTestData observableItemTestData;

    @Autowired
    private MeasureTestData measureTestData;

    public ObservationBuilder defaultBuilder() {
        return Observation.builder()
            .diver(diverTestData.persistedDiver())
            .observableItem(observableItemTestData.persistedObservableItem())
            .measure(measureTestData.persistedMeasure())
            .measureValue(4)
            .observationAttribute(
                ImmutableMap.<String, String>builder()
                    .put("Biomass", "0.7630353218")
                    .build());
    }

}
