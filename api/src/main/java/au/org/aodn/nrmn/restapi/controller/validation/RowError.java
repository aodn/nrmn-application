package au.org.aodn.nrmn.restapi.controller.validation;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RowError {
    Integer id;
    String message;
}
