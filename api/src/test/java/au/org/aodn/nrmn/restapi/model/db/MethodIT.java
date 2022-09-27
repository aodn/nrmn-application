package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.data.model.Method;
import au.org.aodn.nrmn.restapi.data.repository.MethodRepository;
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
class MethodIT {

    @Autowired
    private MethodRepository methodRepository;

    @Autowired
    private MethodTestData methodTestData;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testMapping() {
        Method method = methodTestData.persistedMethod();
        entityManager.clear();
        Method persistedMethod = methodRepository.findById(method.getMethodId()).get();
        assertEquals(method, persistedMethod);

    }

}
