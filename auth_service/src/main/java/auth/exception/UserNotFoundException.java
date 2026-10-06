package auth.exception;

import com.shared.exception.EntityNotFoundException;
import java.util.UUID;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException(UUID id) {
        super("Пользователь с ID " + id + " не найден");
    }

}
