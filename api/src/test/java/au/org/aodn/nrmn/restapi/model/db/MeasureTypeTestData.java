package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.MeasureTypeRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeasureTypeTestData {

    @Autowired
    private MeasureTypeRepository measureTypeRepository;

    public MeasureType persistedMeasureType() {
        val measureType = MeasureType.builder()
            .measureTypeName("Macrocystis Block")
            .isActive(true)
            .build();
        measureTypeRepository.saveAndFlush(measureType);
        return measureType;
    }
}
