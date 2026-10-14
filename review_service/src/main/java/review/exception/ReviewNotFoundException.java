package review.exception;

import com.shared.exception.EntityNotFoundException;

import java.util.UUID;

public class ReviewNotFoundException extends EntityNotFoundException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
    public ReviewNotFoundException(UUID id) {
        super("Review with id " + id + " not found");
    }
}
