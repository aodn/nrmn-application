package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.transaction.Transactional;

@Testcontainers
@SpringBootTest
@Transactional
@ExtendWith(PostgresqlContainerExtension.class)
public class TemplateServiceIT {

    @Test
    @Sql({"/sql/drop_nrmn.sql",
            "/sql/migration.sql",
            "/sql/application.sql",
            "/testdata/FILL_ROLES.sql",
            "/testdata/TEST_USER.sql",
            "/testdata/FILL_DATA.sql",
            "/testdata/FILL_TEMPLATE_DATA.sql"})
    public void verifyTemplateGeneration() {

    }
}
