package au.org.aodn.nrmn.restapi.validation.validators.global.raw;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.model.StagedSurveyMethod;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseGlobalRawValidator;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.tuple.Tuple3;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseMethodCheckRaw extends BaseGlobalRawValidator {
    @Autowired
    StagedRowRepository stagedRowRepo;

    public BaseMethodCheckRaw(String ruleName) {
        super(ruleName);
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedJob job) {
        val stagedSurveyMethods = stagedRowRepo.getStagedSurveyMethods(job.getId());
        val methodsGroupedBySurvey = stagedSurveyMethods.stream().collect(
                Collectors.groupingBy(stagedSurveyMethod -> new Tuple3(stagedSurveyMethod.getSiteCode(),
                        stagedSurveyMethod.getDate(), stagedSurveyMethod.getDepth())));
        return methodsGroupedBySurvey.entrySet()
                                     .stream()
                                     .map((entry) -> {

                                         val methods = entry.getValue().stream()
                                                            .map(StagedSurveyMethod::getMethod)
                                                            .collect(Collectors.toList());

                                         val surveyIdentifier = entry.getKey();

                                         if (valid(methods)) {
                                             return Validated.<StagedRowError, String>valid("surveyGroup " + surveyIdentifier + ": method valid");
                                         } else {
                                             return invalid(job.getId(),
                                                     surveyIdentifier + " has incorrect set of methods: " + methods,
                                                     ValidationLevel.WARNING);
                                         }
                                     }).reduce(Validated.valid("survey methods check: nothing to validate"), (acc, validator) ->
                        acc.combine(Monoids.stringConcat, validator)
                );

    }

    protected abstract boolean valid(List<String> methods);
}
