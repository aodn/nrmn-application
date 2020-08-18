package au.org.aodn.nrmn.restapi.validation.warning;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.RawSurveyEntity;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckEntityRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRefEntityRepository;
import au.org.aodn.nrmn.restapi.validation.BaseValidator;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SiteCodeExists extends BaseValidator {

    SiteRefEntityRepository siteRepo;

    @Autowired
    public SiteCodeExists(ErrorCheckEntityRepository errorRepo,SiteRefEntityRepository siteRepo) {
        super(errorRepo);
        this.siteRepo = siteRepo;
        this.colunmTagert = "Site No";
    }

    @Override
    public Validated<ErrorCheckEntity, String> valid(RawSurveyEntity target) {
        val sites = siteRepo.findBySiteCode(target.SiteNo);
        return warningNotFound(sites, target, target.SiteNo);
    }
}
