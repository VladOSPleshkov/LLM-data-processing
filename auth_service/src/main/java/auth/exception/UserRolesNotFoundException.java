package auth.exception;

import auth.entity.UserRoleId;
import com.shared.exception.EntityNotFoundException;

import java.util.UUID;

public class UserRolesNotFoundException extends EntityNotFoundException {
    public UserRolesNotFoundException(String message) {
        super(message);
    }
    public UserRolesNotFoundException(UUID userId, UUID roleId) { super("Связь пользователя " + userId + " и роли " +roleId+ " не найдена"); }


}
