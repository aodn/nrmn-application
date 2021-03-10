package au.org.aodn.nrmn.restapi.controller.validation;

import lombok.*;

import java.util.List;

@Value
@AllArgsConstructor
public class ValidationErrors {
    private List<ValidationError> errors;
}
