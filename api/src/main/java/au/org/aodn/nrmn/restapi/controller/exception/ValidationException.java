package au.org.aodn.nrmn.restapi.controller.exception;

import au.org.aodn.nrmn.restapi.controller.validation.ValidationErrors;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public class ValidationException extends RuntimeException {

    private final ValidationErrors errors;

    public ValidationException(String object, String property, String message) {
        ValidationError error = new ValidationError(object, property, null, message);
        this.errors = new ValidationErrors(Collections.singletonList(error));
    }

    public ValidationException(List<ValidationError> validationErrors) {
        this.errors = new ValidationErrors(validationErrors);
    }

}
