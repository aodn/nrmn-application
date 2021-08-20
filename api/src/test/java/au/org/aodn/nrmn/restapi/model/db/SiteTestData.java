package au.org.aodn.nrmn.restapi.model.db;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.model.db.Site.SiteBuilder;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;

@Component
public class SiteTestData {

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    LocationTestData locationTestData;

    private int siteNo = 0;

    public Site persistedSite() {
        Site site = defaultBuilder().build();
        siteRepository.saveAndFlush(site);
        return site;
    }

    public SiteBuilder defaultBuilder() {
        siteNo++;
        final Map<String, String> siteAttribute = new HashMap<String, String>();
        siteAttribute.put("ProxCountry", "Antarctica");
        siteAttribute.put("ProtectionStatus", "Fishing");
        return Site.builder()
                   .siteCode("Site " + siteNo)
                   .siteName("Site name" + siteNo)
                   .longitude(-58.5)
                   .latitude(-57.5)
                   .location(locationTestData.persistedLocation())
                   .oldSiteCodes(Arrays.asList(new String[] {"SIT01", "SIT02"}))
                   .state("Graham Land Antarctica")
                   .country("Antarctica")
                   .siteAttribute(siteAttribute)
                   .isActive(true);
    }
}
