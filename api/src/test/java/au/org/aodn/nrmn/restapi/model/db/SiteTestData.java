package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.Site.SiteBuilder;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SiteTestData {

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    LocationTestData locationTestData;

    private int siteNo = 0;

    public Site persistedSite() {
        val site = defaultBuilder().build();
        siteRepository.saveAndFlush(site);
        return site;
    }

    public SiteBuilder defaultBuilder() {
        return Site.builder()
            .siteCode("Site " + ++siteNo)
            .siteName("South Cove South of T310m")
            .longitude(-58.5)
            .latitude(-57.5)
            .location(locationTestData.persistedLocation())
            .siteAttribute(ImmutableMap.<String, Object>builder()
                .put("State", "Graham Land Antarctica")
                .put("Country", "Antarctica")
                .put("ProxCountry", "Antarctica")
                .put("ProtectionStatus", "Fishing")
                .build())
            .isActive(true);
    }
}
