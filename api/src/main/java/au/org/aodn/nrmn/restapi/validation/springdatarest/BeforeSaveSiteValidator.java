package au.org.aodn.nrmn.restapi.validation.springdatarest;

import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("beforeSaveSiteValidator")
public class BeforeSaveSiteValidator implements Validator {

    @Autowired
    private SiteRepository siteRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Site.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        val site = (Site) object;

        val siteWithCodeAndNameExample = Example.of(
                Site.builder()
                    .siteCode(site.getSiteCode())
                    .siteName(site.getSiteName())
                    .build());

        val existingSiteWithCodeAndName = siteRepository.findOne(siteWithCodeAndNameExample);

        if (existingSiteWithCodeAndName.isPresent()
                && !site.getSiteId().equals(existingSiteWithCodeAndName.get().getSiteId())) {
            errors.rejectValue("siteName", "site.exists", "a site with that code and name already exists");
        }
    }
}
