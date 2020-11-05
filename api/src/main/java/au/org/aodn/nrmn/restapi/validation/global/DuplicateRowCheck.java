package au.org.aodn.nrmn.restapi.validation.global;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DuplicateRowCheck extends BaseGlobalValidator {
    @Autowired
    StagedRowRepository stagedRowRepo;

    public DuplicateRowCheck() {
        super("Depth Duplicates");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedJob job) {
      return  stagedRowRepo.findRawRowByReference(job.getReference()).stream()
                .map(duplicate ->
                        Validated.<StagedRowError, String>invalid(
                                new StagedRowError(
                                        new ErrorID(
                                                duplicate.getId(),
                                                job.getId(),
                                                "duplicate depth combo:" + duplicate.getSiteNo() + "|" + duplicate.getDate() + "|" + duplicate.getDepth()
                                        ),
                                        ValidationCategory.GLOBAL,
                                        ruleName,
                                        duplicate)
                        )
                ).reduce(
                Validated.valid("No duplicate dephth combo found"),
                (acc, validator) -> acc.combine(Monoids.stringConcat, validator)
        );
    }

}
