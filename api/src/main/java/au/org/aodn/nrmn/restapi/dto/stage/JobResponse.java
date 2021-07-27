package au.org.aodn.nrmn.restapi.dto.stage;

import java.util.List;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobResponse {
    private StagedJob job;
    private  List<StagedRow> rows;
    private  List<ErrorInput> errorInputs;
}
