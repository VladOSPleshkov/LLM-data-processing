package auth.exception;

import java.util.UUID;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    public UserAlreadyExistsException(UUID id) {
        super("пользователь с таким" + id +"уже существует");
    }

}
