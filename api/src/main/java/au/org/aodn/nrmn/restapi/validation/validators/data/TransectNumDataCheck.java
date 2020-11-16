package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import cyclops.control.Try;
import cyclops.control.Validated;
import lombok.val;

import java.util.Arrays;
import java.util.function.Function;

public class TransectNumDataCheck extends BaseRowValidator {
    public TransectNumDataCheck() {
        super("Depth");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        val result = Try.withCatch(() -> {
            val splitDepth = target.getDepth().split("\\.");
            val transectValue = Integer.parseInt(splitDepth[1]);
            if (transectValue < 1 || transectValue > 4)
                return Validated.<String, String>invalid("Transect value should be 1,2,3,4");
            return Validated.<String, String>valid("Transect value is valid");
        }, Exception.class).orElseGet(() -> Validated.<String, String>invalid("Error while getting the transect value in depth"));

        return result.bimap(errMsg ->
                        new StagedRowError(
                                new ErrorID(
                                        target.getId(),
                                        target.getStagedJob().getId(),
                                        errMsg),
                                ValidationCategory.DATA,
                                columnTarget,
                                target
                        )
                , Function.identity());
    }
}
