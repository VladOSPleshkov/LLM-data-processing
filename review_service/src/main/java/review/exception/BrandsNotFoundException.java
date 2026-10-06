package review.exception;

import com.shared.exception.EntityNotFoundException;

import java.util.UUID;

public class BrandsNotFoundException extends EntityNotFoundException {
    public BrandsNotFoundException(String message) {
        super(message);
    }

    public BrandsNotFoundException(UUID id) {
        super("Brands not found with id: " + id);
    }
}
