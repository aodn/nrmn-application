package au.org.aodn.nrmn.restapi.dto.correction;

import java.io.Serializable;
import java.util.Collection;

import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CorrectionRequestBodyDto implements Serializable {
    private Integer programId;
    private Boolean isMultiple;
    private Collection<StagedRow> rows;
}
