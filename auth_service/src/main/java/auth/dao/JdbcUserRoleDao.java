package auth.dao;

import com.shared.dao.AbstractJdbcDao;
import com.shared.tx.ConnectionHolder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository("userRoleDao")
public class JdbcUserRoleDao extends AbstractJdbcDao implements IUserRoleDao {

    private static final String DB_KEY = "AUTH_DB";

    public JdbcUserRoleDao(ConnectionHolder connectionHolder) {
        super(connectionHolder, DB_KEY);
    }

    @Override
    public void link(UUID userId, UUID roleId, UUID assignedBy) {
        String sql = """
                INSERT INTO user_roles (user_id, role_id, assigned_at, assigned_by)
                VALUES (:userId, :roleId, now(), :assignedBy)
                """;
        execute(true, template -> template.update(sql, new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("roleId", roleId)
                .addValue("assignedBy", assignedBy)));
    }

    @Override
    public void unlink(UUID userId, UUID roleId) {
        String sql = "DELETE FROM user_roles WHERE user_id = :userId AND role_id = :roleId";
        execute(true, template -> template.update(sql, Map.of(
                "userId", userId, "roleId", roleId)));
    }

    @Override
    public List<UUID> findRoleIdsByUserId(UUID userId) {
        String sql = "SELECT role_id FROM user_roles WHERE user_id = :userId";
        return execute(false, template -> template.query(sql, Map.of("userId", userId),
                (rs, rowNum) -> rs.getObject("role_id", UUID.class)));
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        String sql = "DELETE FROM user_roles WHERE user_id = :userId";
        execute(true, template -> template.update(sql, Map.of("userId", userId)));
    }

    @Override
    public void deleteAllByRoleId(UUID roleId) {
        String sql = "DELETE FROM user_roles WHERE role_id = :roleId";
        execute(true, template -> template.update(sql, Map.of("roleId", roleId)));
    }
}