package nl.hva.elections.exception;

import java.time.LocalDateTime;

/**
 * This is our standard DTO for sending structured error responses to the client.
 * Using a consistent structure like this is a best practice for REST APIs.
 */
public class ApiErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    // We can have a couple of constructors for flexibility.
    public ApiErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getters are needed for Jackson to serialize this object to JSON.
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}