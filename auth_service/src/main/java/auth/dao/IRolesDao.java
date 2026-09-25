package auth.dao;

import auth.entity.Roles;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRolesDao {
    Roles save(Roles role);
    Optional<Roles> findById(UUID id);
    Optional<Roles> findByName(String name);
    List<Roles> findAll();
    Roles update(Roles role);
    boolean deleteById(UUID id);
    UUID findOrCreateRole(String name);
    boolean existsById(UUID id);
}