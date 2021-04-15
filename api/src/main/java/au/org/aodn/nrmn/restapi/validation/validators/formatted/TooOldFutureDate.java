package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;
import lombok.val;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;

public class TooOldFutureDate extends BaseFormattedValidator {
    private String dateStr = "";

    public TooOldFutureDate(String dateStr) {
        super("Date");
        this.dateStr = dateStr;
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        val now
                = LocalDate.from(ZonedDateTime.now());
       val earliest = LocalDate.parse(dateStr);
      if ( target.getDate().isAfter(earliest) && target.getDate().isBefore(now)) {
          return Validated.valid("date in range");
      }
        return Validated.invalid(new StagedRowError(
                new ErrorID(target.getId(),
                        target.getRef().getStagedJob().getId(),
                        "Date must be between "+  dateStr" and Today"),
                ValidationCategory.DATA,
                ValidationLevel.WARNING,
                columnTarget,
                target.getRef()));
    }
}
