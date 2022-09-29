package au.org.aodn.nrmn.restapi.model.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.MeasureTypeEntity;
import au.org.aodn.nrmn.restapi.data.model.MeasureTypeEntity.MeasureTypeEntityBuilder;
import au.org.aodn.nrmn.restapi.data.repository.MeasureTypeRepository;

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
