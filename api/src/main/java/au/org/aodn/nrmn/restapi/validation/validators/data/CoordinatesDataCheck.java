package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import com.graphbuilder.geom.Point2d;
import cyclops.companion.Monoids;
import cyclops.control.Try;
import cyclops.control.Validated;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import lombok.val;
import org.locationtech.jts.geom.*;

import java.awt.geom.Point2D;
import java.util.function.Function;
import java.util.stream.Stream;

public class CoordinatesDataCheck extends BaseRowValidator {
    private Site site;

    public CoordinatesDataCheck(Site site) {
        super("Latitude, Longitude");
        this.site = site;
    }

    protected Validated<Tuple2, Double> checkAngle(String name, String angleStr, Double max, Double min) {
        return Try.withCatch(() -> {
            Double angle = Double.parseDouble(angleStr);
            if (angle < min || angle > max)
                return Validated.<Tuple2, Double>invalid(Tuple2.of(name, name + " is not between " + min + " and " + max));
            return Validated.<Tuple2, Double>valid(angle);
        }).orElseGet(() ->
                Validated.invalid(Tuple2.of(name, "not a number"))
        );
    }


    protected Validated<StagedRowError, Seq<Double>> validAngles(StagedRow target) {
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
                                Seq::of
                        ))
                .reduce(
                        Validated.valid(Seq.empty()),
                        (acc, validator) -> acc.combine(Monoids.seqConcat(), validator)
                );
    }

    @Override
    public Validated<StagedRowError, Site> valid(StagedRow target) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel());
        val anglesResult = validAngles(target);

        if (anglesResult.isInvalid())
            return anglesResult.bimap(Function.identity(), seq -> site);

        Seq<Double> latLong = anglesResult.fold(err -> Seq.empty(), Function.identity());
        Coordinate coords = new Coordinate(latLong.getOrElse(1, 0D), latLong.getOrElse(0, 0D));
        val point = geometryFactory.createPoint(coords);

        if (site.getGeom().equalsExact(point, 0.001))
            return Validated.valid(site);

        return Validated.invalid(new StagedRowError(
                new ErrorID(
                        target.getId(),
                        target.getStagedJob().getId(),
                        "Lat and Long didn't match with the Site"
                ),
                ValidationCategory.DATA, "longitude, Lattitude",
                target
        ));
    }
}
