package au.org.aodn.nrmn.restapi.validation.entities;


import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.StagedRowErrorRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SiteCodeExists extends BaseExistingEntity {

    SiteRepository siteRepo;

    @Autowired
    public SiteCodeExists(StagedRowErrorRepository errorRepo, SiteRepository siteRepo) {
        super("Site No");
        this.siteRepo = siteRepo;
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        val sites = siteRepo.findBySiteCode(target.getSiteNo());
        return warningNotFound(sites, target, target.getSiteNo());
    }
}
