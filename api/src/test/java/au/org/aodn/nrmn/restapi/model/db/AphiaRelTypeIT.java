package au.org.aodn.nrmn.restapi.model.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import au.org.aodn.nrmn.restapi.repository.AphiaRelTypeRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
    pattern = ".*TestData"))
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
class AphiaRelTypeIT {

    @Autowired
    private AphiaRelTypeRepository aphiaRelTypeRepository;

    @Autowired
    private AphiaRelTypeTestData aphiaRelTypeTestData;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testMapping() {
        AphiaRelType aphiaRelType = aphiaRelTypeTestData.persistedAphiaRelType();
        entityManager.clear();
        AphiaRelType persistedAphiaRef = aphiaRelTypeRepository.findById(aphiaRelType.getAphiaRelTypeId()).get();
        assertEquals(aphiaRelType, persistedAphiaRef);
    }

}
