package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowErrorRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StageSurveyService {
    @Autowired
    StagedRowRepository rawRepo;

    @Autowired
    StagedJobRepository jobRepo;

    @Autowired
    StagedRowErrorRepository errorRepo;

    public Optional<StagedRow> update(StagedRow update) {
       // errorRepo.deleteAll(update.getErrors());
        return Optional.of(rawRepo.save(update));
    }


    public List<StagedJob> getSurveyFiles() {
        return jobRepo.findAll();

    }

    public List<StagedRow> getRawSurveyFile(String fileID) {
        return rawRepo.findRawSurveyByFileID(fileID);
    }
}
