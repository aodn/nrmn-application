package au.org.aodn.nrmn.restapi.controller.exception;

import au.org.aodn.nrmn.restapi.controller.validation.FormValidationError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ValidationException extends RuntimeException {

    private final List<FormValidationError> errors;

    public ValidationException(String object, String property, String message) {
        FormValidationError error = new FormValidationError(object, property, null, message);
        this.errors = Collections.singletonList(error);
    }

}
