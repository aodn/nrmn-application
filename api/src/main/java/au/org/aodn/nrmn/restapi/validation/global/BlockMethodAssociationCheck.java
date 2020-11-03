package au.org.aodn.nrmn.restapi.validation.global;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.repository.StagedSurveyRepository;
import au.org.aodn.nrmn.restapi.repository.model.SurveyMethodBlock;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BlockMethodAssociationCheck extends BaseGlobalValidator {

    @Autowired
    StagedSurveyRepository stagesSurveyRepo;

    public BlockMethodAssociationCheck() {
        super("Method Block check");
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedJob job) {


        Function<String, ErrorCheck> getGlobalError = (String msg) -> {
            return new ErrorCheck(
                    new ErrorID(
                            null,
                            job.getId(),
                            msg
                    ), ValidationCategory.GLOBAL,
                    ruleName,
                    null
            );
        };
        val aggregatedBlocks = stagesSurveyRepo.findBlockMethods12(job.getId());
        val groupById = aggregatedBlocks.stream().collect(Collectors.groupingBy(
                SurveyMethodBlock::getId));
        val methodExclude = Arrays.asList("3", "4", "5");
        return groupById.entrySet().stream()
                .map(item -> {
                    val blockSum = item.getValue().stream()
                            .map(SurveyMethodBlock::getBlock)
                            .distinct()
                            .sorted()
                            .reduce("", (e1 , e2) -> e1 + e2);

                    if (!blockSum.startsWith("12"))
                        return Validated.<ErrorCheck, String>invalid(getGlobalError.apply(item.getKey() + "invalid block combination"));

                    return Validated.<ErrorCheck, String>valid(item.getKey() + " has valid method/block");
                })
                .reduce(
                        Validated.valid("No element found in " + ruleName),
                        (acc, elem) -> acc.combine(Monoids.stringConcat, elem));

    }
}
