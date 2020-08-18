package au.org.aodn.nrmn.restapi.model.api;

import au.org.aodn.nrmn.restapi.model.db.RawSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    public List<RawSurveyEntity> Rows;
    public String FileID;

}
