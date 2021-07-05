package au.org.aodn.nrmn.restapi.validation.validators.global.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseGlobalFormattedValidator;
import cyclops.companion.Monoids;
import cyclops.control.Validated;

import java.util.ArrayList;
import java.util.List;

public class DuplicateRowCheck extends BaseGlobalFormattedValidator {

    public DuplicateRowCheck() {
        super("Duplicates");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedJob job, List<StagedRowFormatted> rows) {

        List<StagedRowFormatted> duplicatedRows = new ArrayList<>();
        StagedRowFormatted previous = null;
        for (StagedRowFormatted row : rows) {
            if(previous != null && previous.isDuplicateOf(row)){
                duplicatedRows.add(row);
            }
            previous = row;
        }

        if (duplicatedRows.isEmpty())
            return Validated.valid("rows are not duplicated");

        return duplicatedRows
                .stream()
                .map(duplicateRow ->
                        invalid(
                                job.getId(),
                                "Row may be a duplicate of preceding row",
                                ValidationLevel.WARNING,
                                duplicateRow)
                ).reduce(Validated.valid(""), (acc, elem) -> acc.combine(Monoids.stringConcat, elem));
    }
}
