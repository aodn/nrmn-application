package au.org.aodn.nrmn.restapi.validation.provider;


import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.validators.format.IntegerFormatValidation;
import au.org.aodn.nrmn.restapi.validation.validators.global.RLSMethodBlockAssociation;
import au.org.aodn.nrmn.restapi.validation.validators.global.RLSMethodCheck;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service("RLS")
public class RLSValidators implements ValidatorProvider {


    @Autowired
    RLSMethodBlockAssociation rslBlockAssoc;

    @Autowired
    RLSMethodCheck rlsMethodCheck;

    @Override
    public Seq<Tuple2<String, BaseRowValidator>> getRowValidators() {
        return Seq.of(
                Tuple2.of("Method", new IntegerFormatValidation(StagedRow::getMethod, "Method",
                        Arrays.asList(0, 1, 2, 10)))
        );
    }

    @Override
    public Seq<BaseFormattedValidator> getFormattedValidators() {
        return Seq.empty();
    }


    @Override
    public Seq<BaseGlobalValidator> getGlobalValidators() {
        return Seq.of(
                rslBlockAssoc,
                rlsMethodCheck
        );
    }
}
