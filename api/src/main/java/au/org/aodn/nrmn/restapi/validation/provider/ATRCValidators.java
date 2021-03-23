package au.org.aodn.nrmn.restapi.validation.provider;

import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.validators.format.ATRCDepthValidation;
import au.org.aodn.nrmn.restapi.validation.validators.formatted.TooOldFutureDate;
import au.org.aodn.nrmn.restapi.validation.validators.global.ATRCMethodCheck;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ATRC")
public class ATRCValidators implements ValidatorProvider {


    @Autowired
    ATRCMethodCheck atrcMethodCheck;


    @Override
    public Seq<Tuple2<String, BaseRowValidator>> getRowValidators() {
        return Seq.of(Tuple2.of("SurveyNum", new ATRCDepthValidation()));
    }

    @Override
    public Seq<BaseFormattedValidator> getFormattedValidators() {
        return Seq.of(new TooOldFutureDate("1992-01-01"));
    }

    @Override
    public Seq<BaseGlobalValidator> getGlobalValidators() {
        return Seq.empty();
    }
}



