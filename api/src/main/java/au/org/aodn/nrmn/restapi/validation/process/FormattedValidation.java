package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.model.MonoidRowValidation;
import au.org.aodn.nrmn.restapi.validation.model.RowWithValidation;
import au.org.aodn.nrmn.restapi.validation.provider.ValidatorProvider;
import au.org.aodn.nrmn.restapi.validation.validators.formatted.*;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.Seq;

import cyclops.data.tuple.Tuple2;
import lombok.val;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FormattedValidation extends ValidatorHelpers {
    private final BeanFactory beanFactory;

    @Autowired
    public FormattedValidation(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;

    }

    private Seq<BaseFormattedValidator> getCommonValidators() {
        //TODO Add comon formatted validtors
        return Seq.of(new SpeciesNotFound(),
               // new MeasureBetweenL5l95(),
              //  new MeasureUnderLmax(),
                new SpeciesNotSuperseeded());
    }

    private Seq<BaseFormattedValidator> getValidators(StagedJob job) {
        val provider = beanFactory.getBean(job.getProgram().getProgramName(), ValidatorProvider.class);
        val validators = getCommonValidators().appendAll(provider.getFormattedValidators());
        return validators;
    }

    // is the process Valid & return list of stagedRow

    public RowWithValidation<String> process(List<StagedRowFormatted> formattedList, StagedJob job) {
        val validators = getValidators(job);
        val mono = new MonoidRowValidation("", Monoids.stringConcat);
        return formattedList.stream().map(rowFormatted -> {
            val formattedResult = validators
                    .map(v -> v.valid(rowFormatted))
                    .stream()
                    .reduce(
                            Validated.valid(""),
                            (v1, v2) -> v1.combine(Monoids.stringConcat, v2)
                    );
            val errors = toErrorList(formattedResult);
            val stagedRow = rowFormatted.getRef();
            stagedRow.setErrors(errors);
            return new RowWithValidation(Seq.of(stagedRow), formattedResult);
        }).reduce(mono.zero(), mono);
    }
}
