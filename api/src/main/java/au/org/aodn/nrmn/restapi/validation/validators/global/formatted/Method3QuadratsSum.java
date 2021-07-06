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
import java.util.Map;
import java.util.stream.Collectors;

public class Method3QuadratsSum extends BaseGlobalFormattedValidator {
    public Method3QuadratsSum() {
        super("method 3");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedJob job, List<StagedRowFormatted> rows) {

        Map<Object, List<StagedRowFormatted>> transectsMaps = rows.stream()
                .filter(row -> row.getMethod().equals(3))
                .collect(Collectors.groupingBy(StagedRowFormatted::getTransectName));

        List<StagedRowFormatted> transectSumQuadratsUnder50 = transectsMaps.entrySet().stream()
                .filter(entry ->
                        entry.getValue().stream().mapToInt(row -> row.getMeasureJson().getOrDefault(1, 0) ).sum() < 50 ||
                        entry.getValue().stream().mapToInt(row -> row.getMeasureJson().getOrDefault(2, 0) ).sum() < 50 ||
                        entry.getValue().stream().mapToInt(row -> row.getMeasureJson().getOrDefault(3, 0) ).sum() < 50 ||
                        entry.getValue().stream().mapToInt(row -> row.getMeasureJson().getOrDefault(4, 0) ).sum() < 50 ||
                        entry.getValue().stream().mapToInt(row -> row.getMeasureJson().getOrDefault(5, 0) ).sum() < 50)
                .map(entry -> (StagedRowFormatted) entry.getValue().get(0))
                .collect(Collectors.toList());

        if (transectSumQuadratsUnder50.isEmpty())
            return Validated.valid("all transect quadrats sum above 50");


        return transectSumQuadratsUnder50
                .stream()
                .map(transectQuadrats ->
                        invalid(
                                job.getId(),
                                transectQuadrats.getTransectName() + " Quadrats sum under 50",
                                ValidationLevel.BLOCKING,
                                transectQuadrats
                        )
                ).reduce(
                        Validated.valid(""),
                        (acc, elem) -> acc.combine(Monoids.stringConcat, elem)
                );
    }
}
