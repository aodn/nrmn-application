package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import cyclops.control.Try;
import cyclops.control.Validated;
import lombok.val;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.function.Function;

public class BeforeDateCheck extends BaseRowValidator {
    private Date beforeDate;

    public BeforeDateCheck(Date beforeDate) {
        super("Date");
        this.beforeDate = beforeDate;
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        return Try.withCatch(() -> {
            val sdf = new SimpleDateFormat("dd/MM/yyyy");
            val targetDate = sdf.parse(target.getDate());
            val beforeDateStr = sdf.format(beforeDate);
            if (targetDate.compareTo(beforeDate) < 0)
                return Validated.<String, String>invalid("date shouldn't be before " + beforeDateStr);
            return Validated.<String, String>valid("Date is after " + beforeDateStr);
        }, Exception.class).orElseGet(() ->
                Validated.invalid("Error While Parsing date")
        ).bimap(errMsg -> new StagedRowError(
                new ErrorID(
                        target.getId(),
                        target.getStagedJob().getId(),
                        errMsg
                ),
                ValidationCategory.DATA,
                columnTarget,
                target
        ), Function.identity());
    }
}
