package au.org.aodn.nrmn.restapi.validation.validators.base;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;

import java.util.Optional;

public abstract class BaseFormattedValidator {

    public static final double[] FISH_VALUES = {2.5,5,7.5,10,12.5,15,20,25,30,35,40,50,62.5,75,87.5,100,112.5,125,137.5,150,162.5,175,187.5,200,250,300,350,400,450,500,550,600,650,700,750,800,850,900,950,1000};
    public static final double[] INVERT_VALUES = {0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 16, 17, 18, 19, 20, 22, 24, 26, 28, 30};

    protected String columnTarget;

    public BaseFormattedValidator(String columnTarget) {
        this.columnTarget = columnTarget;
    }

    abstract public Validated<StagedRowError, String> valid(StagedRowFormatted target);

    public Validated<StagedRowError, String> invalid(StagedRowFormatted formattedRow, String errorMsg,
                                   ValidationCategory category, ValidationLevel level, Optional<String> target) {
        return Validated.invalid(new StagedRowError(
                new ErrorID(
                        formattedRow.getRef().getId(),
                        formattedRow.getRef().getStagedJob().getId(),
                        errorMsg
                ),
                category,
                level,
                target.orElseGet(() -> columnTarget),
               formattedRow.getRef()));
    }
}
