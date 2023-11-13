package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
    pattern = ".*TestData"))
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
class SiteIT {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SiteTestData siteTestData;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testMapping() {
        Site site = siteTestData.persistedSite();
        entityManager.clear();
        Site persistedSite = siteRepository.findById(site.getSiteId()).get();
        assertEquals(site, persistedSite);
    }
    /**
     * According to spec, the lat / lng needs to round to 5 decimal place.
     */
    @Test
    public void verfiyLatLngRounding() {
        Site site = siteTestData.persistedSite();
        entityManager.clear();
        // Now change the lat/lng to something with more than 5 digit, save it and inspect its rounding
        site.setLatitude(1.23456789);  // Roundup
        site.setLongitude(3.44561111); // Round down

        siteRepository.saveAndFlush(site);

        site = siteRepository.findById(site.getSiteId()).get();

        assertEquals(site.getLatitude(), 1.23457);
        assertEquals(site.getLongitude(), 3.44561);
    }
}
