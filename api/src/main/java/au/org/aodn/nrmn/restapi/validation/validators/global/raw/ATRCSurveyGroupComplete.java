package au.org.aodn.nrmn.restapi.validation.validators.global.raw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.model.StagedSurveyTransect;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseGlobalRawValidator;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.tuple.Tuple3;
import lombok.val;

@Component
public class ATRCSurveyGroupComplete extends BaseGlobalRawValidator {

    @Autowired
    StagedRowRepository stagedRowRepo;

    public ATRCSurveyGroupComplete() {
        super("ATRC Survey Group Complete");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedJob job) {
        val stagedSurveyTransects = stagedRowRepo.getStagedSurveyTransects(job.getId());
        val transectsGroupedBySurveyGroup = stagedSurveyTransects.stream()
                .collect(Collectors.groupingBy(stagedSurveyMethod -> new Tuple3(stagedSurveyMethod.getSiteCode(),
                        stagedSurveyMethod.getDate(), stagedSurveyMethod.getDepth())));
        return transectsGroupedBySurveyGroup.entrySet().stream()
                .map((entry) -> validateSurveyGroup(job, entry.getKey(), entry.getValue()))
                .reduce(Validated.valid("survey group is complete: nothing to validate"),
                        (acc, validator) -> acc.combine(Monoids.stringConcat, validator));
    }

    private Validated<StagedRowError, String> validateSurveyGroup(StagedJob job, Tuple3 surveyGroupKey,
            List<StagedSurveyTransect> transects) {

        List<String> surveyNums = transects.stream().map(StagedSurveyTransect::getSurveyNum)
                .collect(Collectors.toList());

        if (!surveyGroupComplete(surveyNums)) {
            return invalid(job.getId(), surveyGroupKey + " has incorrect set of surveyNums: " + surveyNums,
                    ValidationLevel.BLOCKING);
        }

        val surveys = transects.stream().collect(Collectors.groupingBy(StagedSurveyTransect::getSurveyNum));

        List<Validated<StagedRowError, String>> result = new ArrayList<Validated<StagedRowError, String>>();

        surveys.keySet().stream().forEach(key -> {

            List<StagedSurveyTransect> surveyRecords = surveys.get(key);
            Map<String, List<StagedSurveyTransect>> surveyByMethod = surveyRecords.stream()
                    .collect(Collectors.groupingBy(StagedSurveyTransect::getMethod));

            surveyByMethod.keySet().stream().forEach(method -> {

                List<StagedSurveyTransect> rows = surveyByMethod.get(method);
                String surveyName = rows.get(0).getDepth() + "." + rows.get(0).getSurveyNum();
                String errorMessage = "Survey " + surveyName + " is missing Method " + method;

                // At least one record block 1
                if (!rows.stream().anyMatch(r -> r.getBlock().equalsIgnoreCase("1")))
                    result.add(invalid(job.getId(), errorMessage + " Block 1", ValidationLevel.BLOCKING));

                // For method 1, also check for at least one record on block 2
                if (method.equalsIgnoreCase("1") && !rows.stream().anyMatch(r -> r.getBlock().equalsIgnoreCase("2")))
                    result.add(invalid(job.getId(), errorMessage + " Block 2", ValidationLevel.BLOCKING));
            });
        });

        return result.stream().reduce(Validated.valid(""),
                (acc, validator) -> acc.combine(Monoids.stringConcat, validator));
    }

    private boolean surveyGroupComplete(List<String> surveyNums) {
        return surveyNums.containsAll(Arrays.asList("1", "2", "3", "4"));
    }
}
