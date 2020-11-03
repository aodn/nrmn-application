package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.repository.ObsItemTypeRepository;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecieExists extends BaseRowExistingEntity {
    ObsItemTypeRepository obsItemRepo;

    @Autowired
    SpecieExists(ObsItemTypeRepository obsItemRepo) {
        super("Specie");
        this.obsItemRepo = obsItemRepo;
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedSurvey target) {
        val items = obsItemRepo.findByObsItemTypeName(target.getSpecies());
        return warningNotFound(items, target, target.getBuddy());
    }
}
