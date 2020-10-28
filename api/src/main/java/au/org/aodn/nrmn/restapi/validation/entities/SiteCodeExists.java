package au.org.aodn.nrmn.restapi.validation.entities;


import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SiteCodeExists extends BaseExistingEntity {

    SiteRepository siteRepo;

    @Autowired
    public SiteCodeExists(ErrorCheckRepository errorRepo, SiteRepository siteRepo) {
        super("Site No");
        this.siteRepo = siteRepo;
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedSurvey target) {
        val sites = siteRepo.findBySiteCode(target.getSiteNo());
        return warningNotFound(sites, target, target.getSiteNo());
    }
}
