package au.org.aodn.nrmn.restapi.validation.entities;


import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SiteCodeExists extends BaseRowExistingEntity<Site, SiteRepository> {


    @Autowired
    public SiteCodeExists(SiteRepository siteRepo) {
        super("Site No", siteRepo);
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        return warningNotFound(target, target.getSiteNo());
    }
}
