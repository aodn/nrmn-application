package au.org.aodn.nrmn.restapi.validation.validators.row.formatted;

import java.util.stream.IntStream;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import cyclops.control.Validated;
import lombok.val;

public class Method3QuadratMax50 extends BaseFormattedValidator {
    public Method3QuadratMax50() {
        super("measure");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        if (!target.getMethod().equals(3)) {
            return Validated.valid("not affected");
        }
        val isUnder50 = IntStream.range(1, 6)
                .map(i -> target.getMeasureJson().getOrDefault(i, 0))
                .filter(measure -> measure > 50).toArray();
        if (isUnder50.length == 0)
            return Validated.valid("quadrats under 50");
        return Validated.invalid(new StagedRowError(
                new ErrorID(target.getId(),
                        target.getRef().getStagedJob().getId(),
                        "Quadrats above 50"),
                ValidationCategory.DATA,
                ValidationLevel.BLOCKING,
                columnTarget,
                target.getRef()));
    }
}
