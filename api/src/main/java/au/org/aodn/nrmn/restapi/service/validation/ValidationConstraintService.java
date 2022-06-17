package au.org.aodn.nrmn.restapi.service.validation;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.Observation;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.validation.process.ValidationResultSet;

@Service
public class ValidationConstraintService {

    @Autowired SurveyRepository surveyRepository;

    @Autowired ObservationRepository observationRepository;

    @Autowired DiverRepository diverRepository;

    public Collection<ValidationError> validate(Survey existingSurvey, Collection<StagedRow> rows) {

        ValidationResultSet errors = new ValidationResultSet();

        for(var row : rows) {

            if(!existingSurvey.getDepth().toString().equalsIgnoreCase(row.getDepth()))
                errors.add(row.getId(), ValidationLevel.BLOCKING, "depth", "Cannot Correct Depth");
                
            if(!existingSurvey.getSite().getSiteCode().equalsIgnoreCase(row.getSiteCode()))
                errors.add(row.getId(), ValidationLevel.BLOCKING, "siteCode", "Cannot Correct Site");

            if(!existingSurvey.getSurveyDate().toString().equalsIgnoreCase(row.getDate()))
                errors.add(row.getId(), ValidationLevel.BLOCKING, "date", "Cannot Correct Survey Date");

            if(!existingSurvey.getSurveyTime().toString().equalsIgnoreCase(row.getTime()))
                errors.add(row.getId(), ValidationLevel.BLOCKING, "time", "Cannot Correct Survey Time");

            if(existingSurvey.getVisibility() != Double.parseDouble(row.getVis()))
                errors.add(row.getId(), ValidationLevel.BLOCKING, "vis", "Cannot Correct Visibility");

            if(!existingSurvey.getDirection().toString().equalsIgnoreCase(row.getDirection()))
            errors.add(row.getId(), ValidationLevel.BLOCKING, "direction", "Cannot Correct Direction");

            if(!existingSurvey.getLatitude().toString().equalsIgnoreCase(row.getLatitude()))
            errors.add(row.getId(), ValidationLevel.BLOCKING, "latitude", "Cannot Correct Latitude");

            if(!existingSurvey.getLongitude().toString().equalsIgnoreCase(row.getLongitude()))
            errors.add(row.getId(), ValidationLevel.BLOCKING, "longitude", "Cannot Correct Longitude");


            Optional<Observation> anObservation = Optional.empty();
            var observationId = row.getObservationIds().stream().findFirst();
            if(observationId.isPresent())
                anObservation = observationRepository.findById(observationId.get());

            if(anObservation.isPresent()){
                var observation = anObservation.get();
                if(!observation.getDiver().getInitials().equalsIgnoreCase(row.getDiver()))
                   errors.add(row.getId(), ValidationLevel.BLOCKING, "initials", "Cannot Correct Diver");

                if(!observation.getObservableItem().getObservableItemName().equalsIgnoreCase(row.getSpecies()))
                   errors.add(row.getId(), ValidationLevel.BLOCKING, "species", "Cannot Correct Species Name");

                if(!observation.getSurveyMethod().getMethod().getMethodId().toString().equalsIgnoreCase(row.getMethod()))
                   errors.add(row.getId(), ValidationLevel.BLOCKING, "method", "Cannot Correct Method");
       
                if(!observation.getSurveyMethod().getBlockNum().toString().equalsIgnoreCase(row.getBlock()))
                   errors.add(row.getId(), ValidationLevel.BLOCKING, "block", "Cannot Correct Block");
       
                if(!observation.getSurveyMethod().getSurveyNotDone().toString().equalsIgnoreCase(row.getSurveyNotDone()))
                   errors.add(row.getId(), ValidationLevel.BLOCKING, "surveyNotDone", "Cannot Correct Survey Not Done");
       
                Boolean isInvertSizing = observation.getMeasure().getMeasureType().getMeasureTypeId() == 4;
                if(isInvertSizing != Boolean.parseBoolean(row.getIsInvertSizing()))
                   errors.add(row.getId(), ValidationLevel.BLOCKING, "isInvertSizing", "Cannot Correct Size Class");
            }
        }

        return errors.getAll();
    }
}
