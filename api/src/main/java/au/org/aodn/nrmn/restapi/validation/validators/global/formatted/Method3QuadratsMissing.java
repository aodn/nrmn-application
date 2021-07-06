package au.org.aodn.nrmn.restapi.validation.validators.global.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseGlobalFormattedValidator;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Method3QuadratsMissing extends BaseGlobalFormattedValidator {
    public Method3QuadratsMissing() {
        super("Method 3");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedJob job, List<StagedRowFormatted> rows) {

        Map<Object, List<StagedRowFormatted>> transectsMaps = rows.stream()
                .filter(row -> row.getMethod().equals(3))
                .collect(Collectors.groupingBy(StagedRowFormatted::getTransectName));

        List<StagedRowFormatted> transectSumQuadratsMissing = transectsMaps.entrySet().stream()
                .filter(entry ->
                        entry.getValue().stream().mapToInt(row -> row.getMeasureJson().getOrDefault(1, 0) ).sum() == 0 ||
                        entry.getValue().stream().mapToInt(row -> row.getMeasureJson().getOrDefault(2, 0) ).sum() == 0 ||
                        entry.getValue().stream().mapToInt(row -> row.getMeasureJson().getOrDefault(3, 0) ).sum() == 0 ||
                        entry.getValue().stream().mapToInt(row -> row.getMeasureJson().getOrDefault(4, 0) ).sum() == 0 ||
                        entry.getValue().stream().mapToInt(row -> row.getMeasureJson().getOrDefault(5, 0) ).sum() == 0)
                .map(entry -> (StagedRowFormatted) entry.getValue().get(0))
                .collect(Collectors.toList());

        if (transectSumQuadratsMissing.isEmpty()) {
            return Validated.valid("no Missing quadrats value");
        }

        return transectSumQuadratsMissing.stream().map(keyQuadrats ->
             invalid(
                     job.getId(),
                     keyQuadrats.getTransectName() + " Missing quadrats",
                     ValidationLevel.BLOCKING,
                     keyQuadrats
             )
        ).reduce(
                Validated.valid(""),
                (acc, error) ->acc.combine(Monoids.stringConcat, error)
        );
    }
}
