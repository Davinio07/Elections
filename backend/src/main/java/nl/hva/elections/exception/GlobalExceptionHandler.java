package nl.hva.elections.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * This is our Global Exception Handler. The @RestControllerAdvice annotation
 * allows this class to intercept exceptions thrown from any @RestController
 * across the entire application. This is a best practice for centralized error handling
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Of course, we need a logger here to log the exceptions properly.
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * This handler catches generic Exceptions. It's a catch-all for any unexpected errors.
     * We're logging the full stack trace for debugging purposes but returning a clean,
     * user-friendly error message to the client.
     *
     * @param ex The exception that was thrown.
     * @param request The incoming web request.
     * @return A ResponseEntity containing our structured ApiErrorResponse.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        // We log the error with the full stack trace.
        logger.error("An unexpected error occurred processing request {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        // We create a structured error response. This is for the frontend.
        // We don't want to leak internal details like the stack trace in the response.
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Een onverwachte fout is opgetreden. Probeer het later opnieuw.",
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * We can also add handlers for more specific exceptions.
     * For example, the election parsing can throw IOException.
     * Let's handle that specifically to give a slightly better message.
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiErrorResponse> handleIOException(IOException ex, HttpServletRequest request) {
        logger.error("An I/O error occurred processing request {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "File Processing Error",
                "There was an error reading or processing the election data files.",
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}