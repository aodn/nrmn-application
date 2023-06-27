package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@Testcontainers
@SpringBootTest
@WithTestData
@Transactional
@ExtendWith(PostgresqlContainerExtension.class)
public class EndPointsViewIT {

    final static Logger logger = LoggerFactory.getLogger(EndPointsViewIT.class);

    class EpObservableItems {
        Integer rowNum;
        List values = new ArrayList();

        @Override
        public String toString() {
            return String.format("Row %s, Values %s", rowNum, String.join(",", (List<String>)values.stream().map(f -> String.valueOf(f)).collect(Collectors.toList())));
        }
    }

    JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @BeforeEach
    @Sql({"../../../db/endpoints/CreatePrivateEndpoints.sql", "/testdata/FILL_ENDPOINT_DATA.sql"})
    public void injectTestData() {
        Arrays.asList(
                "ep_rarity_extents","ep_rarity_range","ep_rarity_abundance","ep_rarity_frequency","ep_observable_items")
                .forEach(i -> {
                    logger.info("Populate materialized view " + i);
                    jdbcTemplate.execute("REFRESH MATERIALIZED VIEW nrmn." + i);
                });
    }

    protected void executeSql(String sql, Object[][] results) {
        List<EpObservableItems> objs = jdbcTemplate.query(sql,
                (ResultSet rs, int rowNum) -> {
                    EpObservableItems i = new EpObservableItems();

                    i.rowNum = rowNum;
                    i.values.add(rs.getInt((1)));
                    i.values.add(rs.getString(2));

                    return i;
                });

        logger.info("Query result : {}", objs);
        assertEquals("Total row count for - " + sql, results.length, objs.size());

        for(int i = 0; i < results.length; i++) {
            Object[] o = results[i];

            for(int j = 0; j < o.length; j++) {
                assertEquals(String.format("Item[%s][%s]", i, j), o[j], objs.get(i).values.get(j));
            }
        }
    }
    /**
     * Test on ep_observable_items view
     */
    @Test
    public void verifyEPObservableItems() {
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
    public void verifyEPSpeciesList() {
        Object[][] expected = {
                {330, "Duplicate rubra"},
                {333, "Species 56"},
                {331, "Species 57"},
                {8114, "Ostorhinchus doederleini"},
                {3762, "Acanthostracion polygonius"},
                {2367, "Arenigobius frenatus"},
                {6820, "Acanthurus sp. [pyroferus]"}
        };

        executeSql("select * from nrmn.ep_species_list", expected);
    }
}
