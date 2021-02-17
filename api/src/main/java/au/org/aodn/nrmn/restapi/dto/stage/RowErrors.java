package au.org.aodn.nrmn.restapi.dto.stage;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RowErrors {
    private Long id;
    private List<StagedRowError> errors;
}
