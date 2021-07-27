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
    long siteCount;
    long diverCount;
    long obsItemCount;
    long surveyCount;
    long incompleteSurveyCount;
    Collection<ValidationError> errors;
}
