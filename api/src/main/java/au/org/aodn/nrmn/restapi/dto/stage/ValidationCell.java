package au.org.aodn.nrmn.restapi.dto.stage;

import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationCell {
    private ValidationCategory categoryId;
    private ValidationLevel levelId;
    private String message;
    private String value;
    private Long rowId;
    private String columnName;
}
