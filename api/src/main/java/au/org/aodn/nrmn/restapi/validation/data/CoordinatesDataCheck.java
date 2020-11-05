package au.org.aodn.nrmn.restapi.validation.data;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import cyclops.companion.Monoids;
import cyclops.control.Try;
import cyclops.control.Validated;
import cyclops.data.tuple.Tuple2;
import lombok.val;

import java.util.function.Function;
import java.util.stream.Stream;

public class CoordinatesDataCheck extends BaseRowValidator {

    public CoordinatesDataCheck() {
        super("Latitude, Longitude");
    }

    protected Validated<Tuple2, String> checkAngle(String name, String angleStr, Double max, Double min) {
        return Try.withCatch(() -> {
            Double angle = Double.parseDouble(angleStr);
            if (angle < min || angle > max)
                return Validated.<Tuple2, String>invalid(Tuple2.of(name, name + " is not between " + min + " and " + max));
            return Validated.<Tuple2, String>valid(name + "value is valid");
        }).orElseGet(() ->
                Validated.invalid(Tuple2.of(name, "Lattitdue is not a number"))
        );
    }


    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        val validatedLat = checkAngle("Latitude", target.getLatitude(), 90D, -90D);

        val validatedLong = checkAngle("Longitude", target.getLongitude(), 180D, -180D);

        return Stream.of(validatedLat, validatedLong)
                .map(validator ->
                        validator.bimap(error ->
                                        new StagedRowError(
                                                new ErrorID(
                                                        target.getId(),
                                                        target.getStagedJob().getId(),
                                                        error._2().toString()
                                                ),
                                                ValidationCategory.DATA,
                                                error._1().toString(),
                                                target
                                        ),
                                Function.identity()
                        ))
                .reduce(
                        Validated.valid("empty lat long"),
                        (acc, validator) -> acc.combine(Monoids.stringConcat, validator)
                );
    }
}
