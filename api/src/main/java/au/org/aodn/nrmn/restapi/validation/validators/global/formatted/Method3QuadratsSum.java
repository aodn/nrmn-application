package au.org.aodn.nrmn.restapi.validation.validators.global.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseGlobalFormattedValidator;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.tuple.Tuple2;
import cyclops.data.tuple.Tuple5;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

public class Method3QuadratsSum extends BaseGlobalFormattedValidator {
    public Method3QuadratsSum() {
        super("method 3");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedJob job, List<StagedRowFormatted> rows) {

        val transectsMap = rows.stream()
                .filter(row -> row.getMethod().equals(3))
                .collect(Collectors.groupingBy(row ->
                        row.getSite().getSiteCode()
                                + "-"
                                + row.getDate()
                                + "-"
                                + row.getDepth() + "." + row.getSurveyNum().orElseGet(() -> 0)
                ));

        val transectSumQuadratsUnder50 = transectsMap
                .entrySet().stream()
                .map(entry ->
                        Tuple2.of(
                                entry.getKey(),
                                entry.getValue()
                                        .stream().map(row -> Tuple5.of(
                                        row.getMeasureJson().getOrDefault(1, 0),
                                        row.getMeasureJson().getOrDefault(2, 0),
                                        row.getMeasureJson().getOrDefault(3, 0),
                                        row.getMeasureJson().getOrDefault(4, 0),
                                        row.getMeasureJson().getOrDefault(5, 0)))
                                        .reduce(Tuple5.of(0, 0, 0, 0, 0),
                                                (acc, t) ->
                                                        Tuple5.of(
                                                                acc._1() + t._1(),
                                                                acc._2() + t._2(),
                                                                acc._3() + t._3(),
                                                                acc._4() + t._4(),
                                                                acc._5() + t._5())
                                        )))
                .filter(keyValue ->
                        keyValue._2()._1() < 50 ||
                                keyValue._2()._2() < 50 ||
                                keyValue._2()._3() < 50 ||
                                keyValue._2()._4() < 50 ||
                                keyValue._2()._5() < 50
                ).collect(Collectors.toList());

        if (transectSumQuadratsUnder50.isEmpty())
            return Validated.valid("all transect quadrats sum above 50");


        return transectSumQuadratsUnder50
                .stream()
                .map(transectQuadrats ->
                        invalid(
                                job.getId(),
                                "Transect: " + transectQuadrats._1() + " quadrats sum under 50.",
                                ValidationLevel.BLOCKING)
                ).reduce(Validated.valid(""), (acc, elem) -> acc.combine(Monoids.stringConcat, elem));
    }
}
