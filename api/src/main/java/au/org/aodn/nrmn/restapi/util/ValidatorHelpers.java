package au.org.aodn.nrmn.restapi.util;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import cyclops.companion.Monoids;
import cyclops.companion.Semigroups;
import cyclops.control.Validated;
import cyclops.data.Seq;
import lombok.val;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ValidatorHelpers {
    public<T> Validated<ErrorInput, String> uniqValid(Optional<T> optionalValue , String fieldName) {
        Validated<ErrorInput, String>  valid = Validated.valid(fieldName +" is valid");
        Validated<ErrorInput, String>  invalid =  Validated.invalid(
                        new ErrorInput(fieldName  + " already exists", fieldName)
        );
       return  optionalValue
                .map(value -> invalid)
                .orElse(valid);
    }
  public <E,T> List<E> toErrorList(Validated<E, T>  validator) {
        return  validator.bimap(Seq::of, Function.identity()).foldInvalidLeft(Monoids.seqConcat()).toList();
    }


    public List<ErrorInput> reducetoErrrors(Validated<ErrorInput, String>  ...validators){
       val combineValidator = Arrays.stream(validators).reduce((acc, validator) ->
                    acc.combine( Semigroups.stringJoin(". "), validator)).orElse(Validated.valid("no validators"));
       return toErrorList(combineValidator);
    }
}
