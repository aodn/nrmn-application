package au.org.aodn.nrmn.restapi.dto.stage;

import java.util.Collection;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ValidationResponse {
    StagedJob job;
    
    long rowCount;

    long distinctSiteCount;
    long existingSiteCount;
    Collection<String> mismatchedSites;
    Collection<String> foundSites;

    long diverCount;
    long newDiverCount;

    long obsItemCount;
    long newObsItemCount;

    long surveyCount;
    long incompleteSurveyCount;


    Collection<ValidationError> errors;
}
