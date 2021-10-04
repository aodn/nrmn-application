package au.org.aodn.nrmn.restapi.dto.stage;

import java.util.Collection;

import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationError {

    public ValidationError(ValidationCategory categoryId, ValidationLevel levelId,String message,Collection<Long> rowIds,Collection<String> columnNames){
        setCategoryId(categoryId);
        setLevelId(levelId);
        setMessage(message);
        setRowIds(rowIds);
        setColumnNames(columnNames);
    }

    long id;
    private ValidationCategory categoryId;
    private ValidationLevel levelId;
    private String message;
    private Collection<Long> rowIds;
    private Collection<String> columnNames;
}
