package au.org.aodn.nrmn.restapi.validation.summary;

import au.org.aodn.nrmn.restapi.dto.stage.ErrorMsgSummary;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DefaultSummary {
    public List<ErrorMsgSummary> aggregate(List<StagedRowError> stagedErrors) {

        val groupedByMsg = stagedErrors.stream().
                collect(Collectors.groupingBy(err -> err.getId().getMessage()));

        return groupedByMsg.keySet().stream().map(key -> {
            val errors = groupedByMsg.get(key);
            val ids = errors.stream().map(err -> err.getId().getRowId()).collect(Collectors.toList());
            val maybeFirst = errors.stream().findFirst();
            return new ErrorMsgSummary(
                    key,
                    errors.size() + 1L,
                    ids,
                    maybeFirst.map(StagedRowError::getColumnTarget).orElseGet(() -> ""),
                    maybeFirst.map(StagedRowError::getErrorLevel).orElseGet(() -> ValidationLevel.BLOCKING));
        }).collect(Collectors.toList());
    }

}
