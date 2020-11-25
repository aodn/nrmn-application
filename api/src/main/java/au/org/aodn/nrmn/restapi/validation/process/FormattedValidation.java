package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.provider.ValidatorProvider;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.Seq;

import cyclops.data.tuple.Tuple2;
import lombok.val;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FormattedValidation {
    private final BeanFactory beanFactory;

    @Autowired
    public FormattedValidation(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;

    }

    private Seq<BaseFormattedValidator> getCommonValidators() {
        //TODO Add comon formatted validtors
        return Seq.empty();
    }

    private Seq<BaseFormattedValidator> getValidators(StagedJob job) {
        val provider = beanFactory.getBean(job.getProgram().getProgramName(), ValidatorProvider.class);
        val validators = getCommonValidators().appendAll(provider.getFormattedValidators());
        return validators;
    }

    public Validated<StagedRowError, String> process(List<StagedRowFormatted> formattedList, StagedJob job) {
        val validators = getValidators(job);
        return formattedList.stream().map(rowFormatted ->
                validators
                        .map(v -> v.valid(rowFormatted))
                        .stream()
                        .reduce(
                                Validated.valid(""),
                                (v1, v2) -> v1.combine(Monoids.firstNonNull(), v2)
                        )
        ).reduce(
                Validated.valid(""),
                (v1, v2) -> v1.combine(Monoids.stringConcat, v2));
    }
}
