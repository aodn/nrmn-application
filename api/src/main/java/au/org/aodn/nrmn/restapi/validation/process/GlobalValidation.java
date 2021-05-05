package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseGlobalFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseGlobalRawValidator;
import au.org.aodn.nrmn.restapi.validation.provider.ValidatorProvider;
import au.org.aodn.nrmn.restapi.validation.validators.global.formatted.Method3QuadratsMissing;
import au.org.aodn.nrmn.restapi.validation.validators.global.formatted.Method3QuadratsSum;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.Seq;
import lombok.val;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GlobalValidation {
    private final BeanFactory beanFactory;

    @Autowired
    public GlobalValidation(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    private Seq<BaseGlobalRawValidator> getCommonValidators() {
        //TODO Add comon formatted validtors
        return Seq.empty();
    }

    private Seq<BaseGlobalRawValidator> getRawValidators(StagedJob job) {
        val provider = beanFactory.getBean(job.getProgram().getProgramName(), ValidatorProvider.class);
        val validators = getCommonValidators().appendAll(provider.getGlobalValidators());
        return validators;
    }

    private Seq<BaseGlobalFormattedValidator> getFormattedValidators(StagedJob job) {
        return Seq.of(new Method3QuadratsSum(), new Method3QuadratsMissing());
    }


    public Validated<StagedRowError, String> process(StagedJob job) {
        return getRawValidators(job)
                .map(validator -> validator.valid(job))
                .stream().reduce(
                        Validated.valid(""),
                        (v1, v2) -> v1.combine(Monoids.stringConcat, v2));
    }


    public Validated<StagedRowError, String> processFormatted(StagedJob job, List<StagedRowFormatted> rows) {
        return getFormattedValidators(job)
                .map(validator -> validator.valid(job, rows))
                .stream().reduce(
                        Validated.valid(""),
                        (v1, v2) -> v1.combine(Monoids.stringConcat, v2));
    }

}
