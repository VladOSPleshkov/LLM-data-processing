package auth.exception;

import com.shared.exception.DuplicateEntityException;

public class RoleAlreadyExistException extends DuplicateEntityException {
    public RoleAlreadyExistException(String roleName) {
        super("Роль '" + roleName + "' уже зарегистрирована в системе");
    }
}
