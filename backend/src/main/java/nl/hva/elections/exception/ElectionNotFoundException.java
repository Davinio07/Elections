package nl.hva.elections.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an election with a specific ID cannot be found.
 * * This will be automatically translated to an HTTP 404 Not Found by Spring.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND) // This tells Spring to return 404
public class ElectionNotFoundException extends RuntimeException {

    public ElectionNotFoundException(String message) {
        super(message);
    }

    public ElectionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}