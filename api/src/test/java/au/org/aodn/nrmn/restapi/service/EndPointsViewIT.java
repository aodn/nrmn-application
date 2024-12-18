package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Testcontainers
@SpringBootTest
@Transactional
@ExtendWith(PostgresqlContainerExtension.class)
public class EndPointsViewIT {

    final static Logger logger = LoggerFactory.getLogger(EndPointsViewIT.class);

    static class EpObservableItems {
        Integer rowNum;
        List<Object> values = new ArrayList<>();

        @Override
        public String toString() {
            return String.format(
                    "Row %s, Values %s", rowNum,
                    values.stream().map(String::valueOf).collect(Collectors.joining(","))
            );
        }
    }

    JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    protected void refreshView() {
        Arrays.asList(
                "ep_rarity_extents","ep_rarity_range","ep_rarity_abundance","ep_rarity_frequency","ep_observable_items")
                .forEach(i -> {
                    logger.info("Populate materialized view {}", i);
                    jdbcTemplate.execute("REFRESH MATERIALIZED VIEW nrmn." + i);
                });
    }

    protected void executeSql(String sql, Object[][] results) {
        List<EpObservableItems> objs = jdbcTemplate.query(sql,
                (ResultSet rs, int rowNum) -> {
                    EpObservableItems i = new EpObservableItems();
                    i.rowNum = rowNum;

                    for(int k = 0; k < rs.getMetaData().getColumnCount(); k++) {
                        i.values.add(rs.getObject((k + 1)));
                    }
                    return i;
                });

        logger.info("Query result : {}", objs);
        Assertions.assertEquals(results.length, objs.size(), "Total row count for - " + sql);

        for(int i = 0; i < results.length; i++) {
            Object[] o = results[i];

            // index zero is id
            Optional<EpObservableItems> target = objs.stream().filter(p -> p.values.get(0).equals(o[0])).findFirst();
            Assertions.assertTrue(target.isPresent(), String.format("Value exist for id %s", o[0]));

            for(int j = 0; j < o.length; j++) {
                Assertions.assertEquals(o[j], target.get().values.get(j), String.format("Item[%s][%s]", i, j));
            }
        }
    }
    /**
     * Test on ep_observable_items view
     */
    @Test
    @Sql({"/sql/drop_nrmn.sql",
            "/sql/migration.sql",
            "/sql/application.sql",
            "/testdata/FILL_ROLES.sql",
            "/testdata/TEST_USER.sql",
            "/testdata/FILL_DATA.sql",
            "/testdata/FILL_ENDPOINT_DATA.sql",
            "file:../db/endpoints/CreatePrivateEndpoints.sql"})
    public void verifyEPObservableItems() {
        refreshView();
        Object[][] expected = {
                {332 , "Debris"},
                {330, "Duplicate rubra"},
                {334, "Haliotis rubra"},
                {333, "Species 56"},
                {331, "Species 57"},
                {8114, "Ostorhinchus doederleini"},
                {810, "Apogon doederleini"},
                {3762, "Acanthostracion polygonius"},
                {2367, "Arenigobius frenatus"},
                {6820, "Acanthurus sp. [pyroferus]"},
                {3069, "Acanthurus spp."},
                {8027, "Tripneustes kermadecensis"},
                {4935, "Cypraea annulus"},
                {1526, "Asperaxis karenae"},
                {6285, "Phalacrocorax varius"}
        };

        executeSql("select * from nrmn.ep_observable_items", expected);
    }

    /**
     * Test on ep_species_list view
     */
    @Test
    @Sql({"/sql/drop_nrmn.sql",
            "/sql/migration.sql",
            "/sql/application.sql",
            "/testdata/FILL_ROLES.sql",
            "/testdata/TEST_USER.sql",
            "/testdata/FILL_DATA.sql",
            "/testdata/FILL_ENDPOINT_DATA.sql",
            "file:../db/endpoints/CreatePrivateEndpoints.sql"})
    public void verifyEPSpeciesList() {
        refreshView();
        Object[][] expected = {
                {330, "Duplicate rubra"},
                // {333, "Species 56"}, this item now have superseded in test pack and excluded from this view
                {331, "Species 57"},
                {8114, "Ostorhinchus doederleini"},
                {3762, "Acanthostracion polygonius"},
                {2367, "Arenigobius frenatus"},
                {6820, "Acanthurus sp. [pyroferus]"}
        };

        executeSql("select * from nrmn.ep_species_list", expected);
    }
}
