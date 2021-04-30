package au.org.aodn.nrmn.restapi.validation.validators.global.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseGlobalFormattedValidator;
import cyclops.control.Validated;
import cyclops.data.tuple.Tuple2;
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
                        row.getDepth()
                                + "-"
                                + row.getSite().getSiteCode()
                                + "-"
                                + row.getDate().toEpochDay()
                ));

        val transectSumQuadratsUnder50 = transectsMap
                .entrySet().stream()
                .map(entry ->
                        Tuple2.of(
                                entry.getKey(),
                                entry.getValue()
                                        .stream().map(row ->
                                        row.getMeasureJson().getOrDefault(1, 0) +
                                                row.getMeasureJson().getOrDefault(2, 0) +
                                                row.getMeasureJson().getOrDefault(3, 0) +
                                                row.getMeasureJson().getOrDefault(4, 0) +
                                                row.getMeasureJson().getOrDefault(5, 0)
                                ).reduce(0, Integer::sum)))
                .filter(tuple -> tuple._2() < 50)
                .collect(Collectors.toList());

        if (transectSumQuadratsUnder50.isEmpty())
            return Validated.valid("all transect quadrats sum above 50");


        val transectUnder50 = transectSumQuadratsUnder50
                .stream()
                .map(Tuple2::_1)
                .reduce("", (acc, key) -> acc + "  | " + key);
        return invalid(job.getId(), "Transect: " + transectUnder50 + " quadrats sum under 50.", ValidationLevel.BLOCKING);
    }
}
