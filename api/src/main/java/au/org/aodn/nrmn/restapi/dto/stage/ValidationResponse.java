package au.org.aodn.nrmn.restapi.dto.stage;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ValidationResponse {
    StagedJob job;
    List<RowErrors> errors;
    Map<String, List<ErrorMsgSummary>> summaries;
    List<StagedRowError> errorGlobal;
    List<ErrorInput> errorInputs;
}
