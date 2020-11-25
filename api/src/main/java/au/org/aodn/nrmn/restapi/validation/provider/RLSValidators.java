package au.org.aodn.nrmn.restapi.validation.provider;


import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.validators.format.SurveyNumValidation;
import au.org.aodn.nrmn.restapi.validation.validators.global.RLSMethodBlockAssociation;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("RLS")
public class RLSValidators implements ValidatorProvider {


    @Autowired
    RLSMethodBlockAssociation rslBlockAssoc;

    @Override
    public Seq<Tuple2<String, BaseRowValidator>> getRowValidators() {
        return Seq.of(Tuple2.of("SurveyNum", new SurveyNumValidation(Collections.emptyList())));
    }

    @Override
    public Seq<BaseFormattedValidator> getFormattedValidators() {
        return Seq.empty();
    }


    @Override
    public Seq<BaseGlobalValidator> getGlobalValidators() {

        return Seq.of(rslBlockAssoc);
    }
}
