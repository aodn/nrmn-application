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
import static org.junit.jupiter.api.Assertions.*;

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
            "/testdata/FILL_MEOW_ECOREGION.sql",
            "/testdata/FILL_MATERIALIZED_VIEW_DATA.sql"
    })
    public void verifyEpSurveyList() {
        repository.refreshEpSiteList();
        repository.refreshEpSurveyList();

        List<Tuple> l = repository.getEpSiteList(0, 100);
        assertEquals(3, l.size(), "Size match");

        Object[] expect1 = new Object[] {
                "Australia", "New South Wales", "\"Lord Howe Island\"", "Lord Howe Island Marine Park", "LHI37",
                "Malabar 2", null, -31.5113, 159.05615, 3, 4, 2, 3, "Central Indo-Pacific", "Lord Howe and Norfolk Islands",
                "Lord Howe and Norfolk Islands", "Tropical", "0101000020E610000074B515FBCBE16340DE718A8EE4823FC0",
                "RLS", "No take multizoned"
        };
        assertArrayEquals(expect1, l.get(0).toArray(), "First match");

        Object[] expect2 = new Object[] {
                "Australia", "New South Wales", "\"Lord Howe Island\"", "Lord Howe Island Marine Park", "LHI38",
                "North Bay 2", null, -31.52113, 159.04688000000002, 1, 3, 1, 1, "Central Indo-Pacific", "Lord Howe and Norfolk Islands",
                "Lord Howe and Norfolk Islands", "Tropical", "0101000020E6100000C55A7C0A80E16340E8F692C668853FC0",
                "RLS", "No take multizoned"
        };
        assertArrayEquals(expect2, l.get(1).toArray(), "Second match");

        Object[] expect3 = new Object[] {
                "Australia", "Tasmania", "Kent Group", "Kent Group Marine Park", "KG-S11",
                "Deal Island (Murray Pass)", "1111", -39.46125, 147.31422, 1, 3, 3, 4, "Temperate Australasia", "Southeast Australian Shelf",
                "Cape Howe", "Temperate", "0101000020E6100000F9F719170E6A6240D7A3703D0ABB43C0",
                "ATRC", "No take multizoned"
        };
        assertArrayEquals(expect3, l.get(2).toArray(), "Third match");
    }

    @Test
    @Sql({
            "/sql/drop_nrmn.sql",
            "/sql/migration.sql",
            "/sql/application.sql",
            "/testdata/FILL_ROLES.sql",
            "/testdata/TEST_USER.sql",
            "/testdata/FILL_MEOW_ECOREGION.sql",
            "/testdata/FILL_MATERIALIZED_VIEW_DATA.sql"
    })
    public void verifyEpM2Inverts() {
        repository.refreshEpSiteList();
        repository.refreshEpM2Inverts();

    }

}
