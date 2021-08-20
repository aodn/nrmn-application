package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.MeasureType.MeasureTypeBuilder;
import au.org.aodn.nrmn.restapi.repository.MeasureTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeasureTypeTestData {

    @Autowired
    private MeasureTypeRepository measureTypeRepository;

    private int measureTypeNo = 0;

    public MeasureType persistedMeasureType() {
        MeasureType measureType = defaultBuilder().build();
        measureTypeRepository.saveAndFlush(measureType);
        return measureType;
    }

    public MeasureTypeBuilder defaultBuilder() {
        return MeasureType.builder()
                          .measureTypeName("Measure type " + ++measureTypeNo)
                          .isActive(true);
    }
}
