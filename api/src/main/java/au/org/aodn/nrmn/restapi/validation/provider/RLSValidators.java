package au.org.aodn.nrmn.restapi.validation.provider;


import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.validators.formatted.DebrisZeroObs;
import au.org.aodn.nrmn.restapi.validation.validators.formatted.SpeciesInvertSizing;
import au.org.aodn.nrmn.restapi.validation.validators.formatted.TooOldFutureDate;
import au.org.aodn.nrmn.restapi.validation.validators.global.RLSMethodBlockAssociation;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("RLS")
public class RLSValidators implements ValidatorProvider {


    @Autowired
    RLSMethodBlockAssociation rslBlockAssoc;

    @Override
    public Seq<Tuple2<String, BaseRowValidator>> getRowValidators() {
        return Seq.empty();
    }

    @Override
    public Seq<BaseFormattedValidator> getFormattedValidators() {
        return Seq.of(
                new TooOldFutureDate("2006-01-01"),
           //     new SpeciesInvertSizing(),
                new DebrisZeroObs());
    }
    @Override
    public Seq<BaseGlobalValidator> getGlobalValidators() {

        return Seq.of(rslBlockAssoc);
    }
}
