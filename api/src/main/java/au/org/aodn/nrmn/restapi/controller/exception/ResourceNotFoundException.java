package au.org.aodn.nrmn.restapi.controller.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    Logger logger = LoggerFactory.getLogger(ResourceNotFoundException.class);

    public ResourceNotFoundException(String message) {
        logger.error("{}", message);
    }
}
