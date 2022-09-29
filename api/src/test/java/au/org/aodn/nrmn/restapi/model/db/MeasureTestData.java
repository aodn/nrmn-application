package au.org.aodn.nrmn.restapi.model.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.Measure;
import au.org.aodn.nrmn.restapi.data.model.Measure.MeasureBuilder;
import au.org.aodn.nrmn.restapi.data.repository.MeasureRepository;

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
