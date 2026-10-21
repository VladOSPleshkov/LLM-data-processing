package review.exception;

import com.shared.exception.DuplicateEntityException;

public class CategoryAlreadyExistException extends DuplicateEntityException {
    public CategoryAlreadyExistException(String name) {
        super("Категория с названием '" + name + "' уже существует в системе");
    }
}
