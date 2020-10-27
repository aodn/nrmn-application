package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.repository.DiverRefEntityRepository;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiverExists extends BaseExistingEntity {

    DiverRefEntityRepository diverRepo;

    @Autowired
    DiverExists(DiverRefEntityRepository diverRepo) {
        this.diverRepo = diverRepo;
        this.columnTarget = "Diver";
    }

    @Override
    public Validated<ErrorCheckEntity, String> valid(StagedSurveyEntity target) {
        val divers = diverRepo.findByInitials(target.getDiver());
        return warningNotFound(divers, target, target.getDiver());
    }
}
