package au.org.aodn.nrmn.restapi.controller.exception;

import au.org.aodn.nrmn.restapi.controller.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ValidationException extends RuntimeException {

    private final List<ValidationError> errors;

    public ValidationException(String object, String property, String message) {
        ValidationError error = new ValidationError(object, property, null, message);
        this.errors = Collections.singletonList(error);
    }

}
