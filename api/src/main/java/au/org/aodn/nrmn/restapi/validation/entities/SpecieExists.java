package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.ObsItemType;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.ObsItemTypeRepository;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecieExists extends BaseRowExistingEntity<ObsItemType, ObsItemTypeRepository> {


    @Autowired
    SpecieExists(ObsItemTypeRepository obsItemRepo) {
        super("Specie", obsItemRepo);
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        return warningNotFound(target, target.getSpecies());
    }
}
