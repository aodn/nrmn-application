package au.org.aodn.nrmn.restapi.validation.provider;

import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;

import cyclops.data.Seq;


public interface ValidatorProvider<R extends BaseRowValidator, G extends  BaseGlobalValidator> {
  Seq<R> getRowValidators();
  Seq<R> getExtendedValidators();
}
