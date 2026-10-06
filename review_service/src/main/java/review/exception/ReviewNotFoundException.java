package review.exception;

import java.util.UUID;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
    public ReviewNotFoundException(UUID id) {
        super("Review with id " + id + " not found");
    }
}
