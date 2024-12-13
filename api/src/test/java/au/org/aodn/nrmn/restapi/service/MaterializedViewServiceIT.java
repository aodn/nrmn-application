package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.data.repository.MaterializedViewsRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Verify the materialized view sql correct
 */
@Testcontainers
@SpringBootTest
@Transactional
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
public class MaterializedViewServiceIT {

    @Autowired
    protected MaterializedViewsRepository repository;

    @Test
    @Sql({
            "/sql/drop_nrmn.sql",
            "/sql/migration.sql",
            "/sql/application.sql",
            "/testdata/FILL_ROLES.sql",
            "/testdata/TEST_USER.sql",
            "/testdata/MATERIALIZED_VIEW_DATA.sql"
    })
    public void verifyEpSurveyList() {
        repository.refreshEpSiteList();
        List<Tuple> l = repository.getEpSiteList(0, 100);
        Tuple i = l.get(0);
        System.out.println(i);
    }
}
