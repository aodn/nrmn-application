package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.repository.SiteRefEntityRepository;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SiteCodeExists extends BaseExistingEntity {

    SiteRefEntityRepository siteRepo;

    @Autowired
    public SiteCodeExists(SiteRefEntityRepository siteRepo) {
        this.siteRepo = siteRepo;
        this.columnTarget = "Site No";
    }

    @Override
    public Validated<ErrorCheckEntity, String> valid(StagedSurveyEntity target) {
        val sites = siteRepo.findBySiteCode(target.getSiteNo());
        return warningNotFound(sites, target, target.getSiteNo());
    }
}
