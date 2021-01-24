package au.org.aodn.nrmn.restapi.validation.validators.entities;


import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import cyclops.control.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SiteCodeExists extends BaseRowExistingEntity<Site, SiteRepository> {


    @Autowired
    public SiteCodeExists(SiteRepository siteRepo) {
        super("Site #", siteRepo);
    }

    @Override
    public Validated<StagedRowError, Site> valid(StagedRow target) {
        return checkExists(target, target.getSiteCode(), ValidationLevel.BLOCKING);
    }
}
