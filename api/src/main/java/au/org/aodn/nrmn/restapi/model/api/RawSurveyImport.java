package au.org.aodn.nrmn.restapi.model.api;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class RawSurveyImport {
    public String fileID;
    public List<StagedRow> rows;
}
