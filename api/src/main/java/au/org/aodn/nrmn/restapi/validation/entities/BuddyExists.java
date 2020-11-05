package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuddyExists extends BaseRowExistingEntity {
    DiverRepository diverRepo;

    @Autowired
    BuddyExists(DiverRepository diverRepo) {
        super("Buddy");
        this.diverRepo = diverRepo;
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        val divers = diverRepo.findByInitials(target.getBuddy());
        return warningNotFound(divers, target, target.getBuddy());
    }
}
