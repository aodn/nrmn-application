package au.org.aodn.nrmn.restapi.validation.validators.global;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.model.StagedSurveyMethod;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.tuple.Tuple3;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class ATRCMethodCheck extends BaseGlobalValidator {

    @Autowired
    StagedRowRepository stagedRowRepo;

    public ATRCMethodCheck() {
        super("ATRC Method check");
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

                                         if (methods.containsAll(Arrays.asList("1", "2"))
                                          || methods.stream().anyMatch(s -> Arrays.asList("3", "4", "5").contains(s)))
                                             return Validated.<StagedRowError, String>valid("surveyGroup " + entry.getKey() + ": method valid");

                                         return Validated.<StagedRowError, String>invalid(new StagedRowError(
                                                 new ErrorID(
                                                         null,
                                                         job.getId(),
                                                         entry.getKey() + " has incorrect set of methods: " + methods),
                                                 ValidationCategory.GLOBAL,
                                                 ValidationLevel.WARNING,
                                                 ruleName,
                                                 null
                                         ));
                                     }).reduce(Validated.valid("survey methods check: nothing to validate"), (acc, validator) ->
                        acc.combine(Monoids.stringConcat, validator)
                );

    }
}
