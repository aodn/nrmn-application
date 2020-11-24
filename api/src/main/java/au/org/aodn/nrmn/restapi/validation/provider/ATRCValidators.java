package au.org.aodn.nrmn.restapi.validation.provider;

import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.validators.format.BaseRowFormatValidation;
import au.org.aodn.nrmn.restapi.validation.validators.format.SurveyNumValidation;
import au.org.aodn.nrmn.restapi.validation.validators.global.ATRCMethodCheck;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service("ATRC")
public class ATRCValidators implements ValidatorProvider {

    @Autowired
    ATRCMethodCheck atrcMethodCheck;


    @Override
    public Seq<Tuple2<String, BaseRowValidator>> getRowValidators() {
        return Seq.of(Tuple2.of("SurveyNum", new SurveyNumValidation(Arrays.asList(1, 2, 3, 4))));
    }

    @Override
    public Seq<BaseFormattedValidator> getFormattedValidators() {
        return Seq.empty();
    }

    @Override
    public Seq<BaseGlobalValidator> getGlobalValidators() {
        return Seq.empty();
    }
}



