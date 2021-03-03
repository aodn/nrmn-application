package au.org.aodn.nrmn.restapi.validation.validators.entities;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import cyclops.control.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObservableItemExists extends BaseRowExistingEntity<ObservableItem, ObservableItemRepository> {


    @Autowired
    ObservableItemExists(ObservableItemRepository observableItemRepo) {
        super("Species", observableItemRepo);
    }

    @Override
    public Validated<StagedRowError, ObservableItem> valid(StagedRow target) {
        return checkExists(target, target.getSpecies(), ValidationLevel.BLOCKING);
    }
}
