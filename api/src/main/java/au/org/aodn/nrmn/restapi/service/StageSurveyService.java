package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedSurveyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StageSurveyService {
    @Autowired
    StagedSurveyRepository rawRepo;

    @Autowired
    StagedJobRepository jobRepo;

    @Autowired
    ErrorCheckRepository errorRepo;

    public Optional<StagedSurvey> update(StagedSurvey update) {
       // errorRepo.deleteAll(update.getErrors());
        return Optional.of(rawRepo.save(update));
    }


    public List<StagedJob> getSurveyFiles() {
        return jobRepo.findAll();

    }

    public List<StagedSurvey> getRawSurveyFile(String fileID) {
        return rawRepo.findRawSurveyByFileID(fileID);
    }
}
