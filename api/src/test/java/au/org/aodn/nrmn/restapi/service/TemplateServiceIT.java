package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.data.repository.LetterCodeRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.data.repository.projections.LetterCodeMapping;
import au.org.aodn.nrmn.restapi.data.repository.projections.ObservableItemRow;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
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
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Testcontainers
@SpringBootTest
@Transactional
@ExtendWith(PostgresqlContainerExtension.class)
public class TemplateServiceIT {

    final static Logger logger = LoggerFactory.getLogger(TemplateServiceIT.class);

    @Autowired
    protected LetterCodeRepository letterCodeRepository;

    @Autowired
    protected ObservableItemRepository observableItemRepository;

    protected JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    protected void refreshView() {
        Arrays.asList("ep_site_list")
                .forEach(i -> {
                    logger.info("Populate materialized view " + i);
                    jdbcTemplate.execute("REFRESH MATERIALIZED VIEW nrmn." + i);
                });
    }

    @Test
    @Sql({"/sql/drop_nrmn.sql",
            "/sql/migration.sql",
            "/sql/application.sql",
            "/testdata/FILL_ROLES.sql",
            "/testdata/TEST_USER.sql",
            "/testdata/FILL_DATA.sql",
            "/testdata/FILL_TEMPLATE_DATA.sql"})
    public void verifyMethodWithSiteIds() {
        // Simple case , no supersededby
        List<LetterCodeMapping> r = letterCodeRepository.getForMethodWithSiteIds(2, List.of(3807,3808));

        // Expected result
        Map<String, Long> expectedResult = new HashMap<>() {{
            put("aci",787L);
            put("agr", 1915L);
            put("ama", 2706L);
            // abbreviated_species_code special handle, as original name ends with .ssp
            put("aplysia", 256L);
            put("blenniidae", 698L);
            put("cal", 905L);
            put("cch", 906L);
            put("cirripectes", 565L);
            put("cni", 912L);
            put("comanthus", 909L);
            put("cpo", 920L);
            put("cro", 206L);
            put("cwa", 910L);
            put("drupella", 2502L);
            put("eac", 1418L);
            put("eho", 5968L);
            put("ehoe", 2407L);
            put("enneapterygius", 2508L);
            put("eru", 1383L);
            put("eviota", 1342L);
            put("geu", 973L);
            put("gnu", 1230L);
            put("htu", 239L);
            put("mcl", 1913L);
            put("nsp", 1335L);
            put("nudibranchia", 2653L);
            put("oco", 728L);
            put("psq", 137L);
            put("pst", 1758L);
            put("pta", 186L);
            put("pvo", 566L);
            put("sca", 158L);
            put("sta", 2351L);
            put("tce", 1191L);
            put("tgr", 410L);
            put("tma", 1186L);
        }};

        assertEquals("Size correct", expectedResult.size(), r.size());

        r.stream().forEach(i -> {
            assertEquals(
                    "Lettercode of " + i.getLetterCode(),
                    expectedResult.get(i.getLetterCode()),
                    i.getObservableItemId()
            );
        });

        // Now verify value of method 1, we target an item where supersededby have value, its observable_item_id is 360
        r = letterCodeRepository.getForMethodWithSiteIds(1, List.of(3807,3808));

        // The observable_item_id is 360, supersededBy Chromis kennensis converted to letter code "cke"
        Optional<LetterCodeMapping> t = r.stream().filter(f -> f.getObservableItemId() == 360L).findAny();
        assertEquals("Superseded by name found", "cke", t.get().getLetterCode());
    }
    /**
     * We want to verify that the function getAllWithMethodForSites considered the supersededBy name correctly.
     */
    @Test
    @Sql({"/sql/drop_nrmn.sql",
            "/sql/migration.sql",
            "/sql/application.sql",
            "/testdata/FILL_ROLES.sql",
            "/testdata/TEST_USER.sql",
            "/testdata/FILL_DATA.sql",
            "/testdata/FILL_MEOW_ECOREGION.sql",
            "/testdata/FILL_TEMPLATE_DATA.sql"})
    public void verifyMethodForSiteIds() {
        refreshView();

        // Verify the ep_site_list populate correct info as the getAllWithMethodForSite use this materialized view
        List<AbstractMap.SimpleEntry<String, String>> l = jdbcTemplate.query(
                "select * from nrmn.ep_site_list where site_code = 'LHI52' or site_code = 'LHI43'",
                (rs, i) ->
                     new AbstractMap.SimpleEntry<>(
                            rs.getString(rs.findColumn("site_code")),
                            rs.getString(rs.findColumn("site_name"))
                    )
                );
        assertEquals("Two site found", 2, l.size());

        List<ObservableItemRow> r = observableItemRepository.getAllWithMethodForSites(1, List.of(3807,3808));

        // The supersededBy item have id 360
        Optional<ObservableItemRow> supersededBy = r.stream().filter(f -> f.getObservableItemId() == 360L).findFirst();
        assertTrue("Superseded items included", supersededBy.isPresent());

        assertEquals("Used supersededBy name", "Chromis kennensis", supersededBy.get().getName());
    }
}
