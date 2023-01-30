package au.org.aodn.nrmn.restapi.validation.site;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.data.repository.MeowRegionsRepository;
import au.org.aodn.nrmn.restapi.model.db.MeowRegionTestData;
import au.org.aodn.nrmn.restapi.service.validation.SiteValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;

public class MeowEcoregionTest {

    @Mock
    MeowRegionsRepository meowRegionsRepository;

    @InjectMocks
    SiteValidation siteValidation;

    @Autowired
    private MeowRegionTestData meowRegionTestData;

    @Before
    public void setUp() {
        when(meowRegionsRepository.getEcoregionContains("INSIDE", Arrays.asList("INSIDE1"))).thenReturn(Arrays.asList("INSIDE1"));
        when(meowRegionsRepository.getEcoregionContains("INSIDE", Arrays.asList("OUTSIDE1"))).thenReturn(Collections.emptyList());
    }

    @Test
    public void speciesInMeowTest() {
        // var meow0 = meowRegionTestData.buildWith(0);
        // meowRegionTestData.persistedMeowRegion(meow0);

        // var site = siteTestData.buildWith(0, 0.1, -0.1);
        // var location = locationTestData.buildWith(0);
        // site.setLocation(location);
        // site = siteTestData.persistedSite(site);

        // StagedRowFormatted rowFormatted = new StagedRowFormatted();
        // rowFormatted.setSite(Site.builder().siteCode("A SITE").latitude( -42.886410468013004).longitude(147.33520415427964).build());
        // var error = siteValidation.validateSites(Arrays.asList(rowFormatted));
    }
}
