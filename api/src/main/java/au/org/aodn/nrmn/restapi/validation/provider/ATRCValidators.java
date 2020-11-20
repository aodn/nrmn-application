package au.org.aodn.nrmn.restapi.validation.provider;

import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.validators.data.TransectNumDataCheck;
import au.org.aodn.nrmn.restapi.validation.validators.global.ATRCMethodCheck;
import cyclops.data.Seq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service("ATRC")
public class ATRCValidators<E extends BaseRowValidator> implements ValidatorProvider {
    @Autowired
    ATRCMethodCheck atrcMethodCheck;


    @Override
    public Seq<BaseRowValidator> getRowValidators() {
        return
                Seq.of(
                        new TransectNumDataCheck()
                );
    }

    @Override
    public Seq getExtendedValidators() {
        return Seq.of(atrcMethodCheck);
    }
}

