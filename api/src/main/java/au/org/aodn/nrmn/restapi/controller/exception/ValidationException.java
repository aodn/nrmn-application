package au.org.aodn.nrmn.restapi.controller.exception;

import au.org.aodn.nrmn.restapi.controller.validation.FormValidationError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ValidationException extends RuntimeException {

    Logger logger = LoggerFactory.getLogger(ValidationException.class);

    private final List<FormValidationError> errors;

    public ValidationException(List<FormValidationError> errors) {
        this.errors = errors;
        logger.error("{}", this.errors);
    }

    public ValidationException(String object, String property, String message) {
        FormValidationError error = new FormValidationError(object, property, null, message);
        this.errors = Collections.singletonList(error);
        logger.error("{}", this.errors);
    }

}
