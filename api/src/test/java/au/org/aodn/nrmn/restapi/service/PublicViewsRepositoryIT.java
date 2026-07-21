package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.data.repository.MaterializedViewsRepository;
import au.org.aodn.nrmn.restapi.data.repository.PublicViewsRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verify the public endpoint sql matches the views in CreatePublicEndpoints.sql. The column lists
 * are maintained by hand, so a renamed or dropped column only fails when the query actually runs.
 */
@Testcontainers
@SpringBootTest
@Transactional
@ExtendWith(PostgresqlContainerExtension.class)
public class PublicViewsRepositoryIT {

    @Autowired
    protected PublicViewsRepository publicViewsRepository;

    @Autowired
    protected MaterializedViewsRepository materializedViewsRepository;

    private void verifyPublicView(String viewName, Supplier<Long> count,
            BiFunction<Integer, Integer, List<Tuple>> get) {
        assertDoesNotThrow(() -> assertNotNull(count.get(), viewName + " count"), viewName + " count");
        assertDoesNotThrow(() -> assertNotNull(get.apply(0, 100), viewName + " rows"), viewName + " rows");
    }

    @Test
    @Sql({
            "/sql/drop_nrmn.sql",
            "/sql/migration.sql",
            "/sql/application.sql",
            "file:../db/endpoints/CreatePrivateEndpoints.sql",
            "file:../db/endpoints/CreatePublicEndpoints.sql",
            "/testdata/FILL_ROLES.sql",
            "/testdata/TEST_USER.sql",
            "/testdata/FILL_MEOW_ECOREGION.sql",
            "/testdata/FILL_MATERIALIZED_VIEW_DATA.sql",
    })
    public void verifyPublicEndpointQueries() {
        materializedViewsRepository.refreshEpSiteList();
        materializedViewsRepository.refreshEpSurveyList();
        materializedViewsRepository.refreshEpM1();
        materializedViewsRepository.refreshEpM2CrypticFish();
        materializedViewsRepository.refreshEpM2Inverts();

        verifyPublicView("ep_m0_off_transect_sighting_public",
                publicViewsRepository::countEpM0OffTransectSightingPublic,
                publicViewsRepository::getEpM0OffTransectSightingPublic);

        verifyPublicView("ep_m1_public",
                publicViewsRepository::countEpM1Public,
                publicViewsRepository::getEpM1Public);

        verifyPublicView("ep_m2_cryptic_fish_public",
                publicViewsRepository::countEpM2CrypticFishPublic,
                publicViewsRepository::getEpM2CrypticFishPublic);

        verifyPublicView("ep_m2_inverts_public",
                publicViewsRepository::countEpM2InvertsPublic,
                publicViewsRepository::getEpM2InvertsPublic);

        verifyPublicView("ep_m3_isq_public",
                publicViewsRepository::countEpM3IsqPublic,
                publicViewsRepository::getEpM3IsqPublic);

        verifyPublicView("ep_site_list_public",
                publicViewsRepository::countEpSiteListPublic,
                publicViewsRepository::getEpSiteListPublic);

        verifyPublicView("ep_survey_list_public",
                publicViewsRepository::countEpSurveyListPublic,
                publicViewsRepository::getEpSurveyListPublic);
    }
}
