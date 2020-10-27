package au.org.aodn.nrmn.restapi.validation.warning;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckRepository;
import au.org.aodn.nrmn.restapi.validation.BaseValidator;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiverExists  extends BaseValidator {

    DiverRepository diverRepo;

    @Autowired
    DiverExists(ErrorCheckRepository errorRepo, DiverRepository diverRepo){
        super(errorRepo);
        this.diverRepo = diverRepo;
        this.colunmTagert = "Diver";
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedSurvey target) {
        val divers = diverRepo.findByInitials(target.getDiver());
       return warningNotFound(divers, target, target.getDiver());
    }
}
