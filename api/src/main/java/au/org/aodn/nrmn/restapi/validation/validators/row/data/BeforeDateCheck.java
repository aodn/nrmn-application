package au.org.aodn.nrmn.restapi.validation.validators.row.data;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

import cyclops.control.Try;
import cyclops.control.Validated;
import lombok.val;

import java.time.LocalDate;
import java.util.function.Function;
import java.time.format.DateTimeFormatter;

public class BeforeDateCheck extends BaseFormattedValidator {
    private final LocalDate beforeDate;

    public BeforeDateCheck(LocalDate beforeDate) {
        super("date");
        this.beforeDate = beforeDate;
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        return Try.withCatch(() -> {
            val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            val beforeDateStr = dtf.format(beforeDate);
            if (target.getDate().compareTo(beforeDate) < 0)
                return Validated.<String, String>invalid("date shouldn't be before " + beforeDateStr);
            return Validated.<String, String>valid("Date is after " + beforeDateStr);
        }, Exception.class).orElseGet(() ->
                Validated.invalid("Error While Parsing date")
        ).bimap(errMsg -> new StagedRowError(
                new ErrorID(
                        target.getRef().getId(),
                        target.getRef().getStagedJob().getId(),
                        errMsg
                ),
                ValidationCategory.DATA,
                ValidationLevel.BLOCKING,
                columnTarget,
                target.getRef()
        ), Function.identity());
    }
}
