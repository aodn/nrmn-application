package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.api.ValidationResult;
import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedJobEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckEntityRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobEntityRepository;
import au.org.aodn.nrmn.restapi.repository.StagedSurveyEntityRepository;
import au.org.aodn.nrmn.restapi.validation.warning.DiverExists;
import au.org.aodn.nrmn.restapi.validation.warning.SiteCodeExists;
import cyclops.companion.Functions;
import cyclops.companion.Monoids;
import cyclops.companion.Semigroups;
import cyclops.data.Seq;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    StagedSurveyEntityRepository rawSurveyRepo;

    @Autowired
    StagedJobEntityRepository jobRepo;
    @Autowired
    ErrorCheckEntityRepository errorRepo;


    public Seq<ErrorCheckEntity> processError(StagedSurveyEntity rawSurvey) {
        val res = diverExists.valid(rawSurvey).combine(
                Semigroups.stringJoin(". "),
                siteCodeExists.valid(rawSurvey));
        return res.bimap(Seq::of, (String successMsg) -> successMsg).foldInvalidLeft(Monoids.seqConcat());
    }

    public ValidationResult processList(List<StagedSurveyEntity> entities, String fileID) {
        val currentFile = rawSurveyRepo.findRawSurveyByFileID(fileID);
        val currJob = jobRepo
                .findById(fileID)
                .orElseGet(() -> {
                    StagedJobEntity newjob = jobRepo.save(
                            new StagedJobEntity(
                            fileID,
                            StatusJobType.PENDING,
                            SourceJobType.FILE));
                    return newjob;
                });


        errorRepo.deleteWithFileID(currJob.getId());
        val rawDataWithJob = entities.stream().map(v -> {
            v.setStagedJob(currJob);
            return v;
        }).collect(Collectors.toList());
        val rawSurveys = rawSurveyRepo.saveAll(rawDataWithJob);
        val surveyWithError = rawSurveys.stream().map(raw -> {
            //raw.setErrors(processError(raw).toList());
            return raw;
        }).collect(Collectors.toList());
        return new ValidationResult(surveyWithError, fileID);
    }
}
