package au.org.aodn.nrmn.restapi.dto.stage;

import java.util.List;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobResponse {
    private StagedJob job;
    private  List<StagedRow> rows;
}
