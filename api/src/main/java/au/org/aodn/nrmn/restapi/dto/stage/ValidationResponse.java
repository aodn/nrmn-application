package au.org.aodn.nrmn.restapi.dto.stage;

import java.util.List;
import java.util.Map;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationResponse {
    StagedJob job;
    List<RowErrors> errors;
    Map<String, List<ErrorMsgSummary>> summaries;
    List<StagedRowError> errorGlobal;
    List<ErrorInput> errorInputs;
}
