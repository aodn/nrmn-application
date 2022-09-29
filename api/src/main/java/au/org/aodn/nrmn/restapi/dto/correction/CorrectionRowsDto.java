package au.org.aodn.nrmn.restapi.dto.correction;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

import au.org.aodn.nrmn.restapi.enums.ProgramValidation;

@Data
@NoArgsConstructor
public class CorrectionRowsDto {
    ProgramValidation programValidation;
    Collection<CorrectionRowDto> rows;
}
