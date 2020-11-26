package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.validation.provider.ValidatorProvider;
import com.amazonaws.services.logs.model.GetLogEventsRequest;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.Seq;
import lombok.val;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GlobalValidation {
    private final BeanFactory beanFactory;

    @Autowired
    public GlobalValidation(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    private Seq<BaseGlobalValidator> getCommonValidators() {
        //TODO Add comon formatted validtors
        return Seq.empty();
    }

    private Seq<BaseGlobalValidator> getRawValidators(StagedJob job) {
        val provider = beanFactory.getBean(job.getProgram().getProgramName(), ValidatorProvider.class);
        val validators = getCommonValidators().appendAll(provider.getGlobalValidators());
        return validators;
    }


    public Validated<StagedRowError, String> process(StagedJob job) {
        return getRawValidators(job)
                .map(validator -> validator.valid(job))
                .stream().reduce(
                        Validated.valid(""),
                        (v1, v2) -> v1.combine(Monoids.stringConcat, v2));
    }

}
