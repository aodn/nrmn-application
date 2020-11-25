package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.provider.ValidatorProvider;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.Seq;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormattedValidation {
    private final BeanFactory beanFactory;
    private ValidatorProvider provider;

    @Autowired
    public FormattedValidation(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;

    }

    public Validated<StagedRowError, Object> process(StagedRowFormatted rowFormatted, String programName) {
        provider = beanFactory.getBean(programName, ValidatorProvider.class);
        Seq<BaseFormattedValidator> validators = provider.getRowValidators().appendAll(provider.getGlobalValidators());


        return validators.map(v -> v.valid(rowFormatted)).stream().reduce(
                Validated.valid(""), (v1, v2) -> v1.combine(Monoids.firstNonNull(), v2)
        );
    }
}
