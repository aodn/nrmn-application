package au.org.aodn.nrmn.restapi.validation.validators.global;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.model.RowMethodBlock;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        val surveyGroupMethodCount = stagedRowRepo.getMethodsPerSurvey(job.getId());
        val groupSurvey = surveyGroupMethodCount.stream().collect(Collectors.groupingBy(RowMethodBlock::getId));
        return groupSurvey.entrySet().stream()
                .map((entry) -> {

                    String methodStr = entry.getValue().stream()
                            .map(RowMethodBlock::getMethod)
                            .distinct()
                            .sorted()
                            .reduce("", (e1, e2) -> e1 + e2);

                    if (    methodStr.equals("12") ||
                            methodStr.matches("[0345]*"))
                        return Validated.<StagedRowError, String>valid("surveyGroup " + entry.getKey() + ": method valid");
                    return Validated.<StagedRowError, String>invalid(new StagedRowError(
                            new ErrorID(
                                    null,
                                    job.getId(),
                                    entry.getKey() + "has incorrect set of methods:" + methodStr),
                            ValidationCategory.GLOBAL,
                            ValidationLevel.BLOCKING,
                            ruleName,
                            null
                    ));
                }).reduce(Validated.<StagedRowError, String>valid("survey methods check: nothing to validate"), (acc, validator) ->
                        acc.combine(Monoids.stringConcat, validator)
                );

    }
}
