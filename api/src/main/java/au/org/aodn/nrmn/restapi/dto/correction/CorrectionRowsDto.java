package au.org.aodn.nrmn.restapi.dto.correction;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

@Data
@NoArgsConstructor
public class CorrectionRowsDto implements Serializable {
    Integer programId;
    String programName;
    Collection<Integer> surveyIds;
    Collection<CorrectionRowDto> rows;
}
