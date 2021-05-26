package au.org.aodn.nrmn.restapi.validation.provider;

import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseGlobalRawValidator;

import au.org.aodn.nrmn.restapi.validation.validators.base.BaseRowFormatValidation;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;


public interface ValidatorProvider<
        R extends BaseRowFormatValidation,
        F extends BaseFormattedValidator,
        G extends BaseGlobalRawValidator> {
    Seq<Tuple2<String, R>> getRowValidators();

    Seq<F> getFormattedValidators();

    Seq<G> getGlobalValidators();
}
