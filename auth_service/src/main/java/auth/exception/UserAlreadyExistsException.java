package auth.exception;

import com.shared.exception.DuplicateEntityException;

import java.util.UUID;

public class UserAlreadyExistsException extends DuplicateEntityException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    public UserAlreadyExistsException(UUID id) {
        super("пользователь с таким" + id +"уже существует");
    }

}
