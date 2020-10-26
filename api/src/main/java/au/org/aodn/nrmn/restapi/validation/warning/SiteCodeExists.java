package au.org.aodn.nrmn.restapi.validation.warning;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.validation.BaseValidator;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SiteCodeExists extends BaseValidator {

    SiteRepository siteRepo;

    @Autowired
    public SiteCodeExists(ErrorCheckRepository errorRepo, SiteRepository siteRepo) {
        super(errorRepo);
        this.siteRepo = siteRepo;
        this.colunmTagert = "Site No";
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedSurvey target) {
        val sites = siteRepo.findBySiteCode(target.getSiteNo());
        return warningNotFound(sites, target, target.getSiteNo());
    }
}
