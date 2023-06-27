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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        String name;
        Integer id;
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

    /**
     * Test on ep_observable_items view
     */
    @Test
    public void verifyEPObservableItems() {
        List<EpObservableItems> objs = jdbcTemplate.query("select * from nrmn.ep_observable_items",
                (ResultSet rs, int rowNum) -> {
            EpObservableItems i = new EpObservableItems();

            i.rowNum = rowNum;
            i.id = rs.getInt(1);
            i.name = rs.getString(2);

            return i;
        });

        assertEquals("Total row count for ep_observable_items", 15, objs.size());

        AtomicInteger j = new AtomicInteger(0);
        Arrays.asList("Debris","Duplicate rubra", "Haliotis rubra", "Species 56","Species 57",
                        "Ostorhinchus doederleini","Apogon doederleini","Acanthostracion polygonius",
                        "Arenigobius frenatus","Acanthurus sp. [pyroferus]","Acanthurus spp.",
                        "Tripneustes kermadecensis","Cypraea annulus","Asperaxis karenae","Phalacrocorax varius")
                .forEach(i -> assertEquals("Species name match", i, objs.get(j.getAndIncrement()).name));
    }

    /**
     * Test on ep_species_list view
     */
    @Test
    public void verifyEPSpeciesList() {
        List<EpObservableItems> objs = jdbcTemplate.query("select * from nrmn.ep_species_list",
                (ResultSet rs, int rowNum) -> {
            EpObservableItems i = new EpObservableItems();

            i.rowNum = rowNum;
            i.id = rs.getInt(1);
            i.name = rs.getString(2);

            return i;
        });

        assertEquals("Total row count for ep_species_list", 7, objs.size());

        AtomicInteger j = new AtomicInteger(0);
        Arrays.asList("Duplicate rubra","Species 56","Species 57","Ostorhinchus doederleini"
                        ,"Acanthostracion polygonius","Arenigobius frenatus", "Acanthurus sp. [pyroferus]")
                .forEach(i -> assertEquals("Species name match", i, objs.get(j.getAndIncrement()).name));
    }
}
