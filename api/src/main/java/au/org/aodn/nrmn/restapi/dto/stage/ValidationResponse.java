package au.org.aodn.nrmn.restapi.dto.stage;

import java.util.Collection;
import java.util.Map;

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

    long siteCount;
    long newSiteCount;

    long diverCount;
    long newDiverCount;

    long obsItemCount;
    long newObsItemCount;

    long surveyCount;
    long existingSurveyCount;
    long incompleteSurveyCount;

    Map<String, Boolean> foundSites;

    Collection<ValidationError> errors;
}
