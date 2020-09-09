package au.org.aodn.nrmn.restapi.validation.warning;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.repository.DiverRefEntityRepository;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckEntityRepository;
import au.org.aodn.nrmn.restapi.validation.BaseValidator;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiverExists  extends BaseValidator {

    DiverRefEntityRepository diverRepo;

    @Autowired
    DiverExists(ErrorCheckEntityRepository errorRepo, DiverRefEntityRepository diverRepo){
        super(errorRepo);
        this.diverRepo = diverRepo;
        this.colunmTagert = "Diver";
    }

    @Override
    public Validated<ErrorCheckEntity, String> valid(StagedSurveyEntity target) {
        val divers = diverRepo.findByInitials(target.getDiver());
       return warningNotFound(divers, target, target.getDiver());
    }
}
