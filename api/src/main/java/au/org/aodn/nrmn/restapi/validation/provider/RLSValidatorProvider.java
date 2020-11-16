package au.org.aodn.nrmn.restapi.validation.provider;


import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.validators.entities.SpeciesExists;
import au.org.aodn.nrmn.restapi.validation.validators.global.RLSMethodBlockAssociation;
import cyclops.data.Seq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
@Component
public class RLSValidatorProvider implements ValidatorProvider {
    @Autowired
    RLSMethodBlockAssociation rslBlockAssoc;

    @Override
    public Seq<BaseRowValidator> getRowValidators() {
        return Seq.empty();
    }

    @Override
    public Seq<BaseGlobalValidator> getExtendedValidators() {
        return Seq.of(rslBlockAssoc);
    }

}