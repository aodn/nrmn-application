package au.org.aodn.nrmn.restapi.controller.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
public class ValidationError {
    private String entity;
    private String property;
    private String invalidValue;
    private String message;
}
