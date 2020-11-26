package au.org.aodn.nrmn.restapi.validation.provider;

import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;

import au.org.aodn.nrmn.restapi.validation.validators.format.BaseRowFormatValidation;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;


public interface ValidatorProvider<
        R extends BaseRowFormatValidation,
        F extends BaseFormattedValidator,
        G extends BaseGlobalValidator> {
    Seq<Tuple2<String, R>> getRowValidators();

    Seq<F> getFormattedValidators();

    Seq<G> getGlobalValidators();
}
