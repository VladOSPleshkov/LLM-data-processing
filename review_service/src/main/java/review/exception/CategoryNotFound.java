package review.exception;

import com.shared.exception.EntityNotFoundException;

import java.util.UUID;

public class CategoryNotFound extends EntityNotFoundException {
    public CategoryNotFound(String message) {
        super(message);
    }
    public CategoryNotFound(UUID id) {
      super("Category not found with id: " + id);
    }
}
