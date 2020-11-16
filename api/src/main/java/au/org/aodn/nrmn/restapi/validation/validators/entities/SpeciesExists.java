package au.org.aodn.nrmn.restapi.validation.validators.entities;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.AphiaRefRepository;
import cyclops.control.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpeciesExists extends BaseRowExistingEntity<AphiaRef, AphiaRefRepository> {


    @Autowired
    SpeciesExists(AphiaRefRepository aphiaRepo) {
        super("Specie", aphiaRepo);
    }

    @Override
    public Validated<StagedRowError, AphiaRef> valid(StagedRow target) {
        return warningNotFound(target, target.getSpecies());
    }
}
