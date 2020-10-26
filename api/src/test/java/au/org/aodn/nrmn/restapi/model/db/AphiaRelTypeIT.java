package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.AphiaRelTypeRepository;
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
class AphiaRelTypeIT {

    @Autowired
    private AphiaRelTypeRepository aphiaRelTypeRepository;

    @Autowired
    private AphiaRelTypeTestData aphiaRelTypeTestData;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testMapping() {
        val aphiaRelType = aphiaRelTypeTestData.persistedAphiaRelType();
        entityManager.clear();
        val persistedAphiaRef = aphiaRelTypeRepository.findById(aphiaRelType.getAphiaRelTypeId()).get();
        assertEquals(aphiaRelType, persistedAphiaRef);
    }

}
