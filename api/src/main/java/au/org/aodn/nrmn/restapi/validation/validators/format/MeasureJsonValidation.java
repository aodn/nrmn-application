package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import lombok.val;

import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;

public class MeasureJsonValidation extends BaseRowValidator {
    public MeasureJsonValidation() {
        super("MeasureJson");
    }

    @Override
    public Validated<StagedRowError, HashMap<Integer, Integer>> valid(StagedRow target) {
        Validated<StagedRowError, Seq<Tuple2<Integer, Integer>>> mapValidators = target.getMeasureJson()
                .entrySet()
                .stream().map(entry -> {
                            val intValidator = new IntegerFormatValidation(
                                    r -> entry.getValue(),
                                    entry.getKey().toString(), Collections.emptyList());
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
