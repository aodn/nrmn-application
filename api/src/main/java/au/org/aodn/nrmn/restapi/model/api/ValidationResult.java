package au.org.aodn.nrmn.restapi.model.api;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    public List<StagedRow> rows;
    public String fileID;

}
