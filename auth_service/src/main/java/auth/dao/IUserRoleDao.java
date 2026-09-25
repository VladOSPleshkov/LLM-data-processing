package auth.dao;

import java.util.List;
import java.util.UUID;

public interface IUserRoleDao {
    void link(UUID userId, UUID roleId, UUID assignedBy);
    void unlink(UUID userId, UUID roleId);
    List<UUID> findRoleIdsByUserId(UUID userId);
    void deleteAllByUserId(UUID userId);
    void deleteAllByRoleId(UUID roleId);
}
