package au.org.aodn.nrmn.restapi.validation.validators.global;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.model.RowMethodBlock;
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
public class RLSMethodBlockAssociation extends BaseGlobalValidator {

    @Autowired
    StagedRowRepository stageRowRepo;

    public RLSMethodBlockAssociation() {
        super("RLS Method Block check");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedJob job) {


        Function<String, StagedRowError> getGlobalError = (String msg) -> {
            return new StagedRowError(
                    new ErrorID(
                            null,
                            job.getId(),
                            msg
                    ), ValidationCategory.GLOBAL,
                    ValidationLevel.BLOCKING,
                    ruleName,
                    null
            );
        };
        val aggregatedBlocks = stageRowRepo.findBlockMethods12(job.getId());
        val groupById = aggregatedBlocks.stream().collect(Collectors.groupingBy(
                RowMethodBlock::getId));
        return groupById.entrySet().stream()
                .map(item -> {
                    val blockSum = item.getValue().stream()
                            .map(RowMethodBlock::getBlock)
                            .distinct()
                            .sorted()
                            .reduce("", (e1 , e2) -> e1 + e2);

                    if (!blockSum.startsWith("12"))
                        return Validated.<StagedRowError, String>invalid(getGlobalError.apply(item.getKey() + "invalid block combination"));

                    return Validated.<StagedRowError, String>valid(item.getKey() + " has valid method/block");
                })
                .reduce(
                        Validated.valid("No element found in " + ruleName),
                        (acc, elem) -> acc.combine(Monoids.stringConcat, elem));

    }
}
