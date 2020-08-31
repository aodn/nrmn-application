package au.org.aodn.nrmn.restapi.crud;

import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.composedID.RawSurveyID;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckEntityRepository;
import au.org.aodn.nrmn.restapi.repository.RawSurveyEntityRepository;
import lombok.val;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RawSurveyCRUD {
    @Autowired
    RawSurveyEntityRepository rawRepo;

    @Autowired
    ErrorCheckEntityRepository errorRepo;

    public Optional<StagedSurveyEntity> update(StagedSurveyEntity update) {
        errorRepo.deleteAll(update.getErrors());
        return Optional.of(rawRepo.save(update));
    }


    public List<String> getSurveyFiles() {
        return rawRepo.getFileLIst();

    }

    public List<StagedSurveyEntity> getRawSurveyFile(String fileID) {
        return rawRepo.findRawSurveyByFileID(fileID);
    }
}
