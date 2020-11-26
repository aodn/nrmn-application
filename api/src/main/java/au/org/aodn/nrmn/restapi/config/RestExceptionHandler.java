package au.org.aodn.nrmn.restapi.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Catch constraint violation exceptions from javax.validation annotations and return as errors
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TransactionSystemException.class)
    protected ResponseEntity<?> handleTransactionException(TransactionSystemException ex) throws Throwable {
        Throwable cause = ex.getCause();
        if (!(cause instanceof RollbackException))
            throw cause;
        if (!(cause.getCause() instanceof ConstraintViolationException))
            throw cause.getCause();
        ConstraintViolationException validationException = (ConstraintViolationException) cause.getCause();
        final List<Object> errors = new ArrayList<>();
        validationException.getConstraintViolations().stream().forEach(fieldError -> {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("entity", fieldError.getRootBeanClass().getSimpleName());
            error.put("property", String.valueOf(fieldError.getPropertyPath()));
            error.put("message", fieldError.getMessage());
            errors.add(error);
        });
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("errors", errors);
        return new ResponseEntity<>(body, httpStatus);
    }
}
