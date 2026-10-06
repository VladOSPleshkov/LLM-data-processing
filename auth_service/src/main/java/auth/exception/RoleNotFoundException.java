package auth.exception;

import auth.entity.RoleNames;
import com.shared.exception.EntityNotFoundException;

import java.util.UUID;

public class RoleNotFoundException extends EntityNotFoundException {
    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(UUID id) {
        super("роль с id" + id + "не найдена");
    }
    public static RoleNotFoundException byName(String roleName) {
        return new RoleNotFoundException("Роль с именем '" + roleName + "' не найдена"); }
}
