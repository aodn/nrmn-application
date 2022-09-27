package au.org.aodn.nrmn.restapi.config;

import au.org.aodn.nrmn.restapi.controller.validation.ValidationError;
import au.org.aodn.nrmn.restapi.controller.exception.ValidationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * REST API exception handling - extends/overrides default spring MVC error
 * handling (ResponseEntityExceptionHandler)
 * to return JSON validation errors responses
 */

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /* Handle controller JSR-303 validation errors */

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ValidationError> errors = new ArrayList<>();
        String objectName = ex.getBindingResult().getObjectName();
        ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .forEach(fieldError -> {
                    ValidationError error = ValidationError.builder()
                            .entity(objectName)
                            .property(fieldError.getField())
                            .invalidValue(String.valueOf(fieldError.getRejectedValue()))
                            .message(fieldError.getDefaultMessage())
                            .build();
                    errors.add(error);
                });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /* Handle spring data rest JSR-303 validation errors */

    @ExceptionHandler(TransactionSystemException.class)
    protected ResponseEntity<?> handleTransactionException(TransactionSystemException ex) throws Throwable {
        Throwable cause = ex.getCause();
        if (!(cause instanceof RollbackException))
            throw cause;
        if (!(cause.getCause() instanceof ConstraintViolationException))
            throw cause.getCause();
        List<ValidationError> errors = new ArrayList<>();
        ConstraintViolationException validationException = (ConstraintViolationException) cause.getCause();
        validationException.getConstraintViolations().stream().forEach(fieldError -> {
            ValidationError error = ValidationError.builder()
                    .entity(fieldError.getRootBeanClass().getSimpleName())
                    .property(String.valueOf(fieldError.getPropertyPath()))
                    .message(fieldError.getMessage())
                    .build();
            errors.add(error);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /* Handle custom controller validation errors */

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<?> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(ex.getErrors(), HttpStatus.BAD_REQUEST);
    }
}
