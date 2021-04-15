package au.org.aodn.nrmn.restapi.dto.stage;

import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorMsgSummary {
    private String message;
    private Integer count;
    private List<Long> ids;
    private String columnTarget;
    private ValidationLevel errorLeve;
    private ValidationCategory category;
};
