package au.org.aodn.nrmn.restapi.dto.correction;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CorrectionDiffDto {

    private List<String> deletedRows;

    private List<String> insertedRows;
    
    private List<CorrectionDiffCellDto> cellDiffs;

}
