package nl.hva.elections.exceptions;

public class ElectionNotFoundException extends RuntimeException {

    public ElectionNotFoundException(String message) {
        super(message);
    }
}
