package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.StagedJobEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckEntityRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobEntityRepository;
import au.org.aodn.nrmn.restapi.repository.StagedSurveyEntityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StageSurveyService {
    @Autowired
    StagedSurveyEntityRepository rawRepo;

    @Autowired
    StagedJobEntityRepository jobRepo;

    @Autowired
    ErrorCheckEntityRepository errorRepo;

    public Optional<StagedSurveyEntity> update(StagedSurveyEntity update) {
       // errorRepo.deleteAll(update.getErrors());
        return Optional.of(rawRepo.save(update));
    }


    public List<StagedJobEntity> getSurveyFiles() {
        return jobRepo.findAll();

    }

    public List<StagedSurveyEntity> getRawSurveyFile(String fileID) {
        return rawRepo.findRawSurveyByFileID(fileID);
    }
}
