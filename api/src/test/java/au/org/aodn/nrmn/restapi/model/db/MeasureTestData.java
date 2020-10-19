package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.MeasureRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeasureTestData {

    @Autowired
    private MeasureRepository measureRepository;

    @Autowired
    private MeasureTypeTestData measureTypeTestData;

    public Measure persistedMeasure() {
        val measure = Measure.builder()
            .measureName("10.5cm")
            .measureType(measureTypeTestData.persistedMeasureType())
            .seqNo(21)
            .isActive(true)
            .build();
        measureRepository.saveAndFlush(measure);
        return measure;
    }
}
