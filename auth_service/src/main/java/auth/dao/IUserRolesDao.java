package auth.dao;

import java.util.List;
import java.util.UUID;

public interface IUserRolesDao {
    void linkUserToRole(UUID userId, UUID roleId);
    void unlinkUserFromRole(UUID userId, UUID roleId);
    List<UUID> findRoleIdsByUserId(UUID userId);
    List<UUID> findUserIdsByRoleId(UUID roleId);
    void deleteAllByUserId(UUID userId);
    void deleteAllByRoleId(UUID roleId);
}
