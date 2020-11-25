package au.org.aodn.nrmn.restapi.validation.provider;

import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;

import au.org.aodn.nrmn.restapi.validation.validators.format.BaseRowFormatValidation;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;


public interface ValidatorProvider<
        R extends BaseRowFormatValidation,
        F extends BaseFormattedValidator,
        G extends  BaseGlobalValidator> {
  Seq<Tuple2<String, R>> getRowValidators();
<<<<<<< HEAD
  Seq< F> getFormattedValidators();
=======
  Seq<F> getFormattedValidators();
>>>>>>> b4ac46e7b2a05e4737878ced287db8b00dd5a62e
  Seq<G> getGlobalValidators();
}
