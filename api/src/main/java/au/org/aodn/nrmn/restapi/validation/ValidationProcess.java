package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.api.ValidationResult;
import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;

import au.org.aodn.nrmn.restapi.validation.entities.DiverExists;
import au.org.aodn.nrmn.restapi.validation.entities.SiteCodeExists;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedSurveyRepository;
import cyclops.companion.Monoids;
import cyclops.companion.Semigroups;
import cyclops.data.Seq;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidationProcess {

    @Autowired
    DiverExists diverExists;
    @Autowired
    SiteCodeExists siteCodeExists;

    @Autowired
    StagedSurveyRepository rawSurveyRepo;

    @Autowired
    StagedJobRepository jobRepo;
    @Autowired
    ErrorCheckRepository errorRepo;


    public Seq<ErrorCheck> processError(StagedSurvey rawSurvey) {
        val res = diverExists.valid(rawSurvey).combine(
            Semigroups.stringJoin(". "),
            siteCodeExists.valid(rawSurvey));
        return res.bimap(Seq::of, (String successMsg) -> successMsg).foldInvalidLeft(Monoids.seqConcat());
    }

    public ValidationResult processList(List<StagedSurvey> entities, String fileID) {
        val currentFile = rawSurveyRepo.findRawSurveyByFileID(fileID);
        val job = jobRepo
            .findById(fileID)
            .orElse(new StagedJob(fileID, StatusJobType.FAILED, SourceJobType.FILE, Collections.EMPTY_MAP));
        errorRepo.deleteWithFileID(job.getId());
        val rawDataWithJob = entities.stream().map(v -> {
            v.setStagedJob(job);
            return v;
        }).collect(Collectors.toList());
        val rawSurveys = rawSurveyRepo.saveAll(rawDataWithJob);
        return new ValidationResult(rawSurveys, fileID);
    }
}
