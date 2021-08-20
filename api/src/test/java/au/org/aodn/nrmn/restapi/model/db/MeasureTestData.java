package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.Measure.MeasureBuilder;
import au.org.aodn.nrmn.restapi.repository.MeasureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeasureTestData {

    @Autowired
    private MeasureRepository measureRepository;

    @Autowired
    private MeasureTypeTestData measureTypeTestData;

    public Measure persistedMeasure() {
        Measure measure = defaultBuilder().build();
        measureRepository.saveAndFlush(measure);
        return measure;
    }

    public MeasureBuilder defaultBuilder() {
        return Measure.builder()
            .measureName("10.5cm")
            .measureType(measureTypeTestData.persistedMeasureType())
            .seqNo(21)
            .isActive(true);
    }
}
