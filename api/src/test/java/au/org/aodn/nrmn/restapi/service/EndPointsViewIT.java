package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
public class EndPointsViewIT {

    @BeforeEach
    @Sql({"../../../db/endpoints/CeatePrivateEndpoints.sql", "/testdata/FILL_ENDPOINT_DATA.sql"})
    public void injectTestData() {}

    /**
     * Test on ep_observable_items view
     */
    @Test
    public void verifyEPSpeciesLIst() {}

}
