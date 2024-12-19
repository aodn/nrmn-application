package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.data.repository.MaterializedViewsRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.Tuple;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verify the materialized view sql correct
 */
@Testcontainers
@SpringBootTest
@Transactional
@ExtendWith(PostgresqlContainerExtension.class)
public class MaterializedViewServiceIT {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    protected MaterializedViewsRepository repository;

    @Test
    @Sql({
            "/sql/drop_nrmn.sql",
            "/sql/migration.sql",
            "/sql/application.sql",
            "file:../db/endpoints/CreatePrivateEndpoints.sql",
            "/testdata/FILL_ROLES.sql",
            "/testdata/TEST_USER.sql",
            "/testdata/FILL_MEOW_ECOREGION.sql",
            "/testdata/FILL_MATERIALIZED_VIEW_DATA.sql",
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

        List<Tuple> siteList = repository.getEpSurveyList(0, 100);
        assertEquals(4, siteList.size(), "Survey size match");

        Object[] expect4 = new Object[] {
                812331754, "Australia", "Tasmania", "Kent Group", "Kent Group Marine Park",
                "KG-S11", "Deal Island (Murray Pass)", -39.46125, 147.31422, new BigDecimal("5.4"),
                java.sql.Date.valueOf("2018-06-03"), true, false,
                false, false, "Jan Jansen, Liz Oh", 15.0, null, null, null, null, null, null, null,
                "0101000020E6100000F9F719170E6A6240D7A3703D0ABB43C0",
                "ATRC", "None", null, "1111", "1, 2, 3, 5", null
        };
        assertArrayEquals(expect4, siteList.get(0).toArray(), "Site list first match");

        Object[] expect5 = new Object[] {
                912351270, "Australia", "New South Wales", "\"Lord Howe Island\"", "Lord Howe Island Marine Park",
                "LHI37", "Malabar 2", -31.5113, 159.05615, new BigDecimal("10.0"),
                java.sql.Date.valueOf("2008-02-27"), true, false,
                false, false, null, null, null, "", null, null, null, null, null,
                "0101000020E610000074B515FBCBE16340DE718A8EE4823FC0",
                "RLS", "None", null, null, "1, 2", null
        };
        assertArrayEquals(expect5, siteList.get(1).toArray(), "Site list second match");

        Object[] expect6 = new Object[] {
                912351271, "Australia", "New South Wales", "\"Lord Howe Island\"", "Lord Howe Island Marine Park",
                "LHI38", "North Bay 2", -31.52113, 159.04688000000002, new BigDecimal("1.3"),
                java.sql.Date.valueOf("2008-02-27"), true, false,
                false, false, null, null, null, "", null, null, null, null, null,
                "0101000020E6100000C55A7C0A80E16340E8F692C668853FC0",
                "RLS", "None", null, null, "1", null
        };
        assertArrayEquals(expect6, siteList.get(2).toArray(), "Site list third match");

        Object[] expect7 = new Object[] {
                912351272, "Australia", "New South Wales", "\"Lord Howe Island\"", "Lord Howe Island Marine Park",
                "LHI38", "North Bay 2", -31.52113, 159.04688000000002, new BigDecimal("1.5"),
                java.sql.Date.valueOf("2008-02-27"), true, false,
                false, false, null, null, null, "", null, null, null, null, null,
                "0101000020E6100000C55A7C0A80E16340E8F692C668853FC0",
                "RLS", "None", null, null, "1", null
        };
        assertArrayEquals(expect7, siteList.get(3).toArray(), "Site list third match");
    }

    @Test
    @Sql({
            "/sql/drop_nrmn.sql",
            "/sql/migration.sql",
            "/sql/application.sql",
            "file:../db/endpoints/CreatePrivateEndpoints.sql",
            "/testdata/FILL_ROLES.sql",
            "/testdata/TEST_USER.sql",
            "/testdata/FILL_MEOW_ECOREGION.sql",
            "/testdata/FILL_MATERIALIZED_VIEW_DATA.sql",
    })
    public void verifyEpM2Inverts() {
        repository.refreshEpSiteList();
        repository.refreshEpSurveyList();
        repository.refreshEpRarityExtents();
        repository.refreshEpRarityAbundance();
        repository.refreshEpRarityRange();
        repository.refreshEpRarityFrequency();
        repository.refreshEpObservableItems();
        repository.refreshEpM2Inverts();

        List<Tuple> l = repository.getEpM2Inverts(0, 100);
        assertEquals(7, l.size(), "Size match");

        List<Tuple> t1 = l.stream()
                .filter(i -> i.get("reporting_name") != null)
                .filter(i -> "Plagusia chabrus".equals(i.get("reporting_name").toString())).collect(Collectors.toList());
        assertEquals(1, t1.size(), "t1 size match");

        assertEquals(2, t1.get(0).get("method"), "t1 method match");
        assertEquals(1, t1.get(0).get("block"), "t1 block match");
        assertEquals(BigDecimal.valueOf(7.5), t1.get(0).get("size_class"), "t1 size_class match");
        assertEquals(BigInteger.valueOf(1), t1.get(0).get("total"), "t1 total match");

        List<Tuple> t2 = l.stream()
                .filter(i -> i.get("reporting_name") != null)
                .filter(i -> "Centrostephanus rodgersii".equals(i.get("reporting_name").toString())).collect(Collectors.toList());
        assertEquals(1, t2.size(), "t2 size match");

        assertEquals(2, t2.get(0).get("method"), "t2 0 method match");
        assertEquals(1, t2.get(0).get("block"), "t2 0  block match");
        assertEquals(BigDecimal.valueOf(10), t2.get(0).get("size_class"), "t2 0 size_class match");
        assertEquals(BigInteger.valueOf(13), t2.get(0).get("total"), "t2 0 total match");

        List<Tuple> t3 = l.stream()
                .filter(i -> i.get("reporting_name") != null)
                .filter(i -> "Heliocidaris erythrogramma".equals(i.get("reporting_name").toString())).collect(Collectors.toList());
        assertEquals(2, t3.size(), "t3 size match");

        assertEquals(2, t3.get(0).get("method"), "t3 0 method match");
        assertEquals(1, t3.get(0).get("block"), "t3 0  block match");
        assertEquals(BigDecimal.valueOf(7.5), t3.get(0).get("size_class"), "t3 0 size_class match");
        assertEquals(BigInteger.valueOf(1), t3.get(0).get("total"), "t3 0 total match");

        assertEquals(2, t3.get(1).get("method"), "t3 1 method match");
        assertEquals(1, t3.get(1).get("block"), "t3 1 block match");
        assertEquals(BigDecimal.valueOf(10), t3.get(1).get("size_class"), "t3 1 size_class match");
        assertEquals(BigInteger.valueOf(5), t3.get(1).get("total"), "t3 1 total match");
    }
}
