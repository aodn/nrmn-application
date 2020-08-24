package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.api.ValidationResult;
import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.RawSurveyEntity;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckEntityRepository;
import au.org.aodn.nrmn.restapi.repository.RawSurveyEntityRepository;
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
import java.util.List;
import java.util.Optional;

@Component
public class ValidationProcess {

    @Autowired
    DiverExists diverExists;
    @Autowired
    SiteCodeExists siteCodeExists;

    @Autowired
    RawSurveyEntityRepository rawSurveyRepo;
    @Autowired
    ErrorCheckEntityRepository errorRepo;


    public Seq<ErrorCheckEntity> processError(RawSurveyEntity rawSurvey) {
        val res = diverExists.valid(rawSurvey).combine(
                Semigroups.stringJoin(". "),
                siteCodeExists.valid(rawSurvey));
        return res.bimap(Seq::of, Functions.identity()).foldInvalidLeft(Monoids.seqConcat());
    }

    public ValidationResult processList(List<RawSurveyEntity> entities, String fileID) {
        val currentFile =rawSurveyRepo.findRawSurveyByFileID(fileID);
        if (!currentFile.isEmpty())
            return new ValidationResult(currentFile, fileID);
        errorRepo.deleteWithFileID(fileID);
        Seq<RawSurveyEntity> rawDataWithFile = Seq.fromStream(entities.stream()).map(v -> {
            v.rid.fileID = fileID;
            return v;
        });

        val rawSurveys = rawSurveyRepo.saveAll(rawDataWithFile);
        val surveySeq = Seq.fromStream(rawSurveys.stream());
        val surveyWithError = surveySeq.map(raw -> {
            raw.Errors  = processError(raw).toList();
            return raw;
        }).toList();
        return new ValidationResult( surveyWithError, fileID);
    }
}
