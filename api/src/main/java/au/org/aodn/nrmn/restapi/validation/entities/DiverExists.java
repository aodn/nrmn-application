package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowErrorRepository;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiverExists extends BaseExistingEntity {

    DiverRepository diverRepo;

    @Autowired
    DiverExists(StagedRowErrorRepository errorRepo, DiverRepository diverRepo) {
        super("Diver");
        this.diverRepo = diverRepo;
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        val divers = diverRepo.findByInitials(target.getDiver());
        return warningNotFound(divers, target, target.getDiver());
    }
}
