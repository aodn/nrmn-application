package au.org.aodn.nrmn.restapi.validation.validators.global;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.StagedSurveyTransect;
import au.org.aodn.nrmn.restapi.repository.model.StagedSurveyMethod;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.tuple.Tuple3;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ATRCSurveyGroupComplete extends BaseGlobalValidator {

    @Autowired
    StagedRowRepository stagedRowRepo;

    public ATRCSurveyGroupComplete() {
        super("ATRC Survey Group Complete");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedJob job) {
        val stagedSurveyTransects = stagedRowRepo.getStagedSurveyTransects(job.getId());
        val transectsGroupedBySurveyGroup = stagedSurveyTransects.stream().collect(
                Collectors.groupingBy(stagedSurveyMethod -> new Tuple3(stagedSurveyMethod.getSiteCode(),
                        stagedSurveyMethod.getDate(), stagedSurveyMethod.getDepth())));
        return transectsGroupedBySurveyGroup.entrySet()
                                     .stream()
                                     .map((entry) -> {

                                         val surveyNums = entry.getValue().stream()
                                                            .map(StagedSurveyTransect::getSurveyNum)
                                                            .collect(Collectors.toList());

                                         val surveyGroupKey = entry.getKey();

                                         if (surveyGroupComplete(surveyNums)) {
                                             return Validated.<StagedRowError, String>valid("surveyGroup " + surveyGroupKey + ": is complete");
                                         } else {
                                             return invalid(job.getId(),
                                                     surveyGroupKey + " has incorrect set of surveyNums: " + surveyNums,
                                                     ValidationLevel.BLOCKING);
                                         }
                                     }).reduce(Validated.valid("survey group is complete: nothing to validate"), (acc,
                                      validator) ->
                        acc.combine(Monoids.stringConcat, validator)
                );
    }

    private boolean surveyGroupComplete(List<String> surveyNums) {
        return surveyNums.containsAll(Arrays.asList());
    }
}
