package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.Site.SiteBuilder;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
        siteNo++;

        return Site.builder()
                   .siteCode("Site " + siteNo)
                   .siteName("Site name" + siteNo)
                   .longitude(-58.5)
                   .latitude(-57.5)
                   .location(locationTestData.persistedLocation())
                   .oldSiteCodes(Arrays.asList(new String[] {"SIT01", "SIT02"}))
                   .siteAttribute(ImmutableMap.<String, String>builder()
                           .put("State", "Graham Land Antarctica")
                           .put("Country", "Antarctica")
                           .put("ProxCountry", "Antarctica")
                           .put("ProtectionStatus", "Fishing")
                           .build())
                   .isActive(true);
    }
}
