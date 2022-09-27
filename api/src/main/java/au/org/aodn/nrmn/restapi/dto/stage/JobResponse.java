package au.org.aodn.nrmn.restapi.dto.stage;

import java.util.List;

import au.org.aodn.nrmn.db.model.StagedJob;
import au.org.aodn.nrmn.db.model.StagedRow;
import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobResponse {
    private StagedJob job;
    private  List<StagedRow> rows;
    private  List<ErrorInput> errorInputs;
}
