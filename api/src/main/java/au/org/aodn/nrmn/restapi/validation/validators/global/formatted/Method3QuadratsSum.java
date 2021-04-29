package au.org.aodn.nrmn.restapi.validation.validators.global.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseGlobalFormattedValidator;
import cyclops.control.Validated;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

public class Method3QuadratsSum extends BaseGlobalFormattedValidator {
    public Method3QuadratsSum() {
        super("method 3");
    }

    @Override
    public Validated<StagedRowError, String> valid(List<StagedRowFormatted> rows) {

        val transectsMap = rows.stream()
                .filter(row -> row.getMethod().equals(3))
                .collect(Collectors.groupingBy(row ->
                        row.getDepth()
                        + "-"
                        + row.getSite().getSiteCode()
                        + "-"
                        + row.getDate().toEpochDay()
                ));

       val transectSumQuadrats  = transectsMap
                .values().stream()
                .map(list ->
                        list.stream().map(row ->
                                row.getMeasureJson().getOrDefault(1,0) +
                                        row.getMeasureJson().getOrDefault(2,0) +
                                        row.getMeasureJson().getOrDefault(3,0)+
                                        row.getMeasureJson().getOrDefault(4,0)+
                                        row.getMeasureJson().getOrDefault(5,0)
                                ).reduce(0, Integer::sum))
               .collect(Collectors.toList());
       
        return Validated.valid("not imolemented");
    }
}
