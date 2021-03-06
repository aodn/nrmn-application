package au.org.aodn.nrmn.restapi.dto.stage;

import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class ValidationError {
    private ValidationCategory categoryId;
    private ValidationLevel levelId;
    private String message;
    private Collection<Long> rowIds;
    private Collection<String> columnNames;
}
