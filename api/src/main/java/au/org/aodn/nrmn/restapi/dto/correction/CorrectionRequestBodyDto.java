package au.org.aodn.nrmn.restapi.dto.correction;

import java.util.Collection;

import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CorrectionRequestBodyDto {
    private Boolean isExtended;
    private Integer programId;
    private Collection<StagedRow> rows;
}
