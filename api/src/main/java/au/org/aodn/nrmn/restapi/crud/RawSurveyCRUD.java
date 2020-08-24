package au.org.aodn.nrmn.restapi.crud;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.RawSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.composedID.RawSurveyID;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckEntityRepository;
import au.org.aodn.nrmn.restapi.repository.RawSurveyEntityRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RawSurveyCRUD {
    @Autowired
    RawSurveyEntityRepository rawRepo;

    @Autowired
    ErrorCheckEntityRepository errorRepo;

    public Optional<RawSurveyEntity> update(RawSurveyEntity newRaw) {
        val surveys = rawRepo.findByRid(new RawSurveyID(newRaw.rid.id, newRaw.rid.fileID));
        for(RawSurveyEntity s : surveys) {
           errorRepo.deleteAll(s.Errors);
            s.Diver = newRaw.Diver;
            s.SiteNo = newRaw.SiteNo;
            s.Block = newRaw.Block;
            s.Date = newRaw.Date;
            s.Method = newRaw.Method;
            s.Species = newRaw.Species;
            s.Buddy = newRaw.Buddy;
            s.Code = newRaw.Code;
            s.CommonName = newRaw.CommonName;
            s.Direction = newRaw.Direction;
            s.Inverts = newRaw.Inverts;
            s.IsInvertSizing = newRaw.IsInvertSizing;
            s.L5 = newRaw.L5;
            s.L95 = newRaw.L95;
            s.MeasureJson = newRaw.MeasureJson;
            s.PQs = newRaw.PQs;
            s.Total = newRaw.Total;
            s.Time = newRaw.Time;
            return Optional.of(rawRepo.save(s));
        };
    return Optional.empty();
    }


    public List<String> getSurveyFiles() {
        return rawRepo.getFileLIst();

    }

    public List<RawSurveyEntity> getRawSurveyFile(String fileID) {
        return rawRepo.findRawSurveyByFileID(fileID);
    }
}
