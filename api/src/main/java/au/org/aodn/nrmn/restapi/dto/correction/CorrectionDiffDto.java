package au.org.aodn.nrmn.restapi.dto.correction;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class CorrectionDiffDto implements Serializable {

    private List<String> deletedRows;

    private List<String> insertedRows;

    private List<CorrectionDiffCellDto> cellDiffs;

}
