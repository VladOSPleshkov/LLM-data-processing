package auth.dao;

import auth.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserDao {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByPasswordHash(String passwordHash);
    List<User> findAll();
    User update(User user);
    boolean deleteById(UUID id);
    boolean existsById(UUID id);
}