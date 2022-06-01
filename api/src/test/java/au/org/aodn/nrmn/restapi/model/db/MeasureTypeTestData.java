package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.MeasureTypeEntity.MeasureTypeEntityBuilder;
import au.org.aodn.nrmn.restapi.repository.MeasureTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeasureTypeTestData {

    @Autowired
    private MeasureTypeRepository measureTypeRepository;

    private int measureTypeNo = 0;

    public MeasureTypeEntity persistedMeasureType() {
        MeasureTypeEntity measureType = defaultBuilder().build();
        measureTypeRepository.saveAndFlush(measureType);
        return measureType;
    }

    public MeasureTypeEntityBuilder defaultBuilder() {
        return MeasureTypeEntity.builder()
                          .measureTypeName("Measure type " + ++measureTypeNo)
                          .isActive(true);
    }
}
