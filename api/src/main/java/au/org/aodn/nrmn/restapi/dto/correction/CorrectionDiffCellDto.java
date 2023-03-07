package au.org.aodn.nrmn.restapi.dto.correction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionDiffCellDto {

    private String diffRowId;

    private String columnName;

    private String oldValue;
    
    private String newValue;

}
