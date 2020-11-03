package au.org.aodn.nrmn.restapi.validation.global;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.repository.StagedSurveyRepository;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DuplicateSurveyCheck extends BaseGlobalValidator {
    @Autowired
    StagedSurveyRepository stagedSurveyRepo;

    public DuplicateSurveyCheck() {
        super("Depth Duplicates");
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedJob job) {
      return  stagedSurveyRepo.findRawSurveyByFileID(job.getId()).stream()
                .map(duplicate ->
                        Validated.<ErrorCheck, String>invalid(
                                new ErrorCheck(
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
