package au.org.aodn.nrmn.restapi.validation.model;

import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.Seq;
import cyclops.function.Monoid;
import cyclops.function.Semigroup;

public class MonoidRowValidation<T> implements Monoid<RowWithValidation<T>> {

    private T defaultValue;
    private Semigroup merger;

    public MonoidRowValidation(T zero, Semigroup merger) {
        super();
        defaultValue = zero;
        this.merger = merger;
    }

    @Override
    public RowWithValidation<T> zero() {
        return new RowWithValidation(Seq.empty(), Validated.valid(defaultValue));
    }

    @Override
    public RowWithValidation<T> apply(RowWithValidation rowValid1, RowWithValidation rowValid2) {
        return new RowWithValidation(
                rowValid1.getRows().appendAll(rowValid2.getRows()),
                rowValid1.valid.combine(merger, rowValid2.getValid())
        );
    }
}
