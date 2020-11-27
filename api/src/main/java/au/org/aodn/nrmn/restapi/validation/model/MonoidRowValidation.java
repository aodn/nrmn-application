package au.org.aodn.nrmn.restapi.validation.model;

import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.Seq;
import cyclops.function.Monoid;

public class MonoidRowValidation implements Monoid<RowWithValidation<String>> {
    @Override
    public RowWithValidation<String> zero() {
        return new RowWithValidation(Seq.empty(), Validated.valid(""));
    }

    @Override
    public RowWithValidation<String> apply(RowWithValidation rowValid1, RowWithValidation rowValid2) {
        return new RowWithValidation(
                rowValid1.getRows().appendAll(rowValid2.getRows()),
                rowValid1.valid.combine(Monoids.stringConcat, rowValid2.getValid())
                );
    }
}
