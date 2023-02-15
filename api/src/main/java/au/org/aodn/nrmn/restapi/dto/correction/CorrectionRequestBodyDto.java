package au.org.aodn.nrmn.restapi.dto.correction;

import java.util.List;

import au.org.aodn.nrmn.restapi.data.model.StagedJobLog;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CorrectionRequestBodyDto {
    private Integer programId;
    private Boolean isMultiple;
    private List<StagedJobLog> logs;
    private List<StagedRow> rows;
}
