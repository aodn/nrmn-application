package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.util.MeasureUtil;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseRowValidator;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import lombok.val;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class MeasureJsonValidation extends BaseRowValidator {
    public MeasureJsonValidation() {
        super("MeasureJson");
    }

    @Override
    public Validated<StagedRowError, HashMap<Integer, Integer>> valid(StagedRow target) {
        if (!Optional.ofNullable(target.getMeasureJson()).isPresent()){
            return Validated.valid(new HashMap<>());
        }
        Validated<StagedRowError, Seq<Tuple2<Integer, Integer>>> mapValidators = target.getMeasureJson()
                .entrySet()
                .stream().map(entry -> {
                            if (entry.getValue().trim().isEmpty()){
                                return Validated.<StagedRowError, Seq<Tuple2<Integer, Integer>>>valid(Seq.empty());
                            }
                            val col = MeasureUtil.getMeasureName(entry.getKey(), target.getIsInvertSizing() != null && target.getIsInvertSizing().equalsIgnoreCase("1"));
                            val intValidator = new IntegerFormatValidation(
                                    r -> entry.getValue(),
                                    col,
                                    Collections.emptyList());

                            return intValidator.valid(target).map(i ->
                                    Seq.of(Tuple2.of(entry.getKey(), i)));
                        }
                ).reduce(
                        Validated.valid(Seq.empty()),
                        (v1, v2) -> v1.combine(Monoids.seqConcat(), v2));

        return mapValidators.bimap(Function.identity(), tupleSeq -> {
            HashMap<Integer, Integer> hashMap = new HashMap<>();
            tupleSeq.forEach(entry -> hashMap.put(entry._1(), entry._2()));
            return hashMap;
        });
    }
}
