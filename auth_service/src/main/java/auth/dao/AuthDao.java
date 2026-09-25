package auth.dao;

import java.util.Optional;
import java.util.UUID;

public interface AuthDao {
    UUID saveUser(String username, String passwordHash);

    // Поиск хэша пароля по имени пользователя для валидации при логине
    Optional<String> findPasswordHashByUsername(String username);

    // Получение ID пользователя по имени
    Optional<UUID> findIdByUsername(String username);
}
