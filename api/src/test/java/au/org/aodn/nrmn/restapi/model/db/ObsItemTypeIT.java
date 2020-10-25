package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.ObsItemTypeRepository;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
    pattern = ".*TestData"))
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("emptydb")
class ObsItemTypeIT {

    @Autowired
    ObsItemTypeRepository obsItemTypeRepository;

    @Autowired
    ObsItemTypeTestData obsItemTypeTestData;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testMapping() {
        val obsItemType = obsItemTypeTestData.persistedObsItemType();
        entityManager.clear();
        val persistedObsItemType = obsItemTypeRepository.findById(obsItemType.getObsItemTypeId()).get();
        assertEquals(obsItemType, persistedObsItemType);
    }
}
