package au.org.aodn.nrmn.restapi.dto.correction;

import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
public class CorrectionRequestBodyDto {
    private Boolean isExtended;
    private ProgramValidation programValidation;
    private Collection<StagedRow> rows;
}
