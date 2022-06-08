package au.org.aodn.nrmn.restapi.service.validation;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRowPutDto;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.validation.process.ValidationResultSet;

@Service
public class NullValidationService {

    @Autowired SurveyRepository surveyRepository;

    @Autowired ObservationRepository observationRepository;

    @Autowired DiverRepository diverRepository;

    public Collection<ValidationError> validate(Survey existingSurvey, Collection<CorrectionRowPutDto> rowUpdates) {

        ValidationResultSet errors = new ValidationResultSet();

        // TODO: Where does the diver come from?
        var divers = diverRepository.findAllById(rowUpdates.stream().map(r -> r.getDiverId()).collect(Collectors.toList())).stream().map(d -> d.getDiverId()).collect(Collectors.toSet());

        // TODO: Verify that reference data has not changed
        for(var row : rowUpdates) {

            if(!divers.contains(row.getDiverId()))
                errors.add(row.getId(), ValidationLevel.BLOCKING, "initials", "Cannot Correct Diver");

            if(existingSurvey.getDepth().intValue() != row.getDepth().intValue())
                errors.add(row.getId(), ValidationLevel.BLOCKING, "depth", "Cannot Correct Depth");

            // errors.add(row.getId(), ValidationLevel.BLOCKING, "siteCode", "Cannot Correct Site");
            // errors.add(row.getId(), ValidationLevel.BLOCKING, "surveyDate", "Cannot Correct Survey Date");
            // errors.add(row.getId(), ValidationLevel.BLOCKING, "surveyTime", "Cannot Correct Survey Time");
            // errors.add(row.getId(), ValidationLevel.BLOCKING, "visibility", "Cannot Correct Visibility");
            // errors.add(row.getId(), ValidationLevel.BLOCKING, "direction", "Cannot Correct Direction");
            // errors.add(row.getId(), ValidationLevel.BLOCKING, "latitude", "Cannot Correct Latitude");
            // errors.add(row.getId(), ValidationLevel.BLOCKING, "longitude", "Cannot Correct Longitude");
            // errors.add(row.getId(), ValidationLevel.BLOCKING, "observableItemName", "Cannot Correct Species Name");
            // errors.add(row.getId(), ValidationLevel.BLOCKING, "methodId", "Cannot Correct Method");
            // errors.add(row.getId(), ValidationLevel.BLOCKING, "blockNum", "Cannot Correct Block");
            // errors.add(row.getId(), ValidationLevel.BLOCKING, "surveyNotDone", "Cannot Correct Survey Not Done");
            // errors.add(row.getId(), ValidationLevel.BLOCKING, "useInvertSizing", "Cannot Correct Size Class");
        }

        return errors.getAll();
    }
}
