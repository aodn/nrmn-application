package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseRowValidator;
import cyclops.control.Validated;

/*
   waiting for more spec
 */
public class SurveyNotDoneCheck extends BaseRowValidator {

    public SurveyNotDoneCheck(String columnTarget) {
        super(columnTarget);
    }

    @Override
    public Validated<StagedRowError, Boolean> valid(StagedRow target) {
//        val filteredEntry = target.getMeasureJson().entrySet().stream().filter(entry -> StringUtils.isNumeric(entry.getValue())
//                && !entry.getValue().trim().equals("0")).collect(Collectors.toList());
//        if (target.getSpecies().toLowerCase().trim().equals("No Species Found")) {
//            if (filteredEntry.size() == 0) {
//                return Validated.valid(true);
//            }
//            return Validated.invalid(new StagedRowError(
//                    new ErrorID(target.getId(),
//                            target.getStagedJob().getId(),
//                            target.getSpecies() + " should have no measure"),
//                    ValidationCategory.DATA,
//                    ValidationLevel.WARNING,
//                    columnTarget,
//                    target));
//        }
        return Validated.valid(false);
    }
}
