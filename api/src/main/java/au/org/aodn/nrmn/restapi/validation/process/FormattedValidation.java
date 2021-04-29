package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.model.MonoidRowValidation;
import au.org.aodn.nrmn.restapi.validation.model.RowWithValidation;
import au.org.aodn.nrmn.restapi.validation.provider.ValidatorProvider;
import au.org.aodn.nrmn.restapi.validation.validators.formatted.*;
import au.org.aodn.nrmn.restapi.validation.validators.entities.SurveyExists;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.Seq;

import lombok.val;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FormattedValidation extends ValidatorHelpers {
    private final BeanFactory beanFactory;
    private SurveyRepository surveyRepository;

    @Autowired
    public FormattedValidation(BeanFactory beanFactory, SurveyRepository surveyRepository) {
        this.beanFactory = beanFactory;
        this.surveyRepository = surveyRepository;
    }

    private Seq<BaseFormattedValidator> getCommonValidators() {

        return Seq.of(new SpeciesNotFound(),
//                new MeasureBetweenL5l95(),
//                new MeasureUnderLmax(),
//                new SpeciesNotSuperseeded(),
                new TotalCheckSum(),
                new SurveyExists(surveyRepository)
        );
    }

    private Seq<BaseFormattedValidator> getValidators(StagedJob job) {
        val provider = beanFactory.getBean(job.getProgram().getProgramName(), ValidatorProvider.class);
        val validators = getCommonValidators().appendAll(provider.getFormattedValidators());
        return validators;
    }


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
