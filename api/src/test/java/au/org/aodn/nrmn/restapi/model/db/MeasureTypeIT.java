package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.data.model.MeasureTypeEntity;
import au.org.aodn.nrmn.restapi.data.repository.MeasureTypeRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
    pattern = ".*TestData"))
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
class MeasureTypeIT {

    @Autowired
    private MeasureTypeRepository measureTypeRepository;

    @Autowired
    private MeasureTypeTestData measureTypeTestData;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testMapping() {
        MeasureTypeEntity measureType = measureTypeTestData.persistedMeasureType();
        entityManager.clear();
        MeasureTypeEntity persistedMeasureType = measureTypeRepository.findById(measureType.getMeasureTypeId()).get();
        assertEquals(measureType, persistedMeasureType);
    }
}
