package au.org.aodn.nrmn.restapi.validation.summary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.dto.stage.ErrorMsgSummary;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import lombok.val;

@Component
public class DefaultSummary {
    public Map<String,List<ErrorMsgSummary>> aggregate(List<StagedRowError> stagedErrors) {

        val groupedByMsg = stagedErrors.stream().
                collect(Collectors.groupingBy(err -> err.getId().getMessage()));

        return groupedByMsg.keySet().stream().map(key -> {
            val errors = groupedByMsg.get(key);
            val ids = errors.stream().map(err -> err.getId().getRowId()).collect(Collectors.toList());
            val maybeFirst = errors.stream().findFirst();
            return new ErrorMsgSummary(
                    key,
                    errors.size(),
                    ids,
                    maybeFirst.map(StagedRowError::getColumnTarget).orElseGet(() -> ""),
                    maybeFirst.map(StagedRowError::getErrorLevel).orElseGet(() -> ValidationLevel.BLOCKING),
                    maybeFirst.map(StagedRowError::getErrorType).orElseGet(() -> ValidationCategory.DATA));
        }).collect(Collectors.groupingBy(err -> err.getColumnTarget()));
    }

}
