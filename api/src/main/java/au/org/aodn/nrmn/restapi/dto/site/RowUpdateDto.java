package au.org.aodn.nrmn.restapi.dto.site;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RowUpdateDto {

    private Long rowId;

    private StagedRow row;
}
