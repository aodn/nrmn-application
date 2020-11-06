package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import cyclops.control.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuddyExists extends BaseRowExistingEntity<Diver, DiverRepository> {

    @Autowired
    public BuddyExists(DiverRepository buddyRepo) {
        super("Buddy", buddyRepo);
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        return warningNotFound(target, target.getBuddy());
    }
}
