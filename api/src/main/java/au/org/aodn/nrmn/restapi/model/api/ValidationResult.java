package au.org.aodn.nrmn.restapi.model.api;

import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    public List<StagedSurveyEntity> Rows;
    public String FileID;

}
