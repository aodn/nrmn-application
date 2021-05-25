package au.org.aodn.nrmn.restapi.validation.validators.row.entities;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseRowExistingEntity;
import cyclops.control.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObservableItemExists extends BaseRowExistingEntity<ObservableItem, ObservableItemRepository> {


    @Autowired
    ObservableItemExists(ObservableItemRepository observableItemRepo) {
        super("species", observableItemRepo);
    }

    @Override
    public Validated<StagedRowError, ObservableItem> valid(StagedRow target) {
        String speciesName = target.getSpecies();
        if (speciesName != null && speciesName.equalsIgnoreCase("Survey not done")) {
            return Validated.valid(null);
        } else {
            return checkExists(target, speciesName, ValidationLevel.BLOCKING);
        }
    }
}
