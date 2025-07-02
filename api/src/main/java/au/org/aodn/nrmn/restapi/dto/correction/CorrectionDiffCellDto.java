package au.org.aodn.nrmn.restapi.dto.correction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionDiffCellDto implements Serializable {

    private String diffRowId;

    private String columnName;

    private String speciesName;

    private String oldValue;

    private String newValue;

}
