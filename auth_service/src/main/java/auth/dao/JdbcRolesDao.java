package auth.dao;

import auth.entity.RoleNames;
import auth.entity.Roles;
import com.shared.dao.AbstractJdbcDao;
import com.shared.exception.EntityNotFoundException;
import com.shared.tx.ConnectionHolder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.*;
@Repository("rolesDao")
public class JdbcRolesDao extends AbstractJdbcDao implements IRolesDao {

    private static final String DB_KEY = "AUTH_DB";
    private final AuthDbMapper authDbMapper;

    public JdbcRolesDao(ConnectionHolder connectionHolder, AuthDbMapper authDbMapper) {
        super(connectionHolder, DB_KEY);
        this.authDbMapper = authDbMapper;
    }
    private MapSqlParameterSource getParamSource(Roles role) {
        return new MapSqlParameterSource()
                .addValue("id", role.getId(), Types.OTHER)
                .addValue("name", role.getName().getValue())
                .addValue("description", role.getDescription());
    }

    @Override
    public Roles save(Roles role) {
        UUID id = role.getId() != null ? role.getId() : UUID.randomUUID();
        Roles toPersist = role.toBuilder().id(id).build();

        String sql = "INSERT INTO roles (id, name, description) VALUES (:id, :name, :description)" +
                " ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description RETURNING id;";
        UUID actualId = execute(true, template ->
                template.queryForObject(sql, getParamSource(toPersist), UUID.class)); // Возвращаем объект Roles с актуальным ID
        return toPersist.toBuilder().id(actualId).build();
    }

    @Override
    public Optional<Roles> findById(UUID id) {
        String sql = "SELECT * FROM roles WHERE id = :id";
        List<Roles> results = execute(false, template ->
                template.query(sql, Map.of("id", id), (rs, rowNum) -> authDbMapper.roleFromResultSet(rs)));
        return results.stream().findFirst();
    }

    @Override
    public Optional<Roles> findByName(String name) {
        String sql = "SELECT * FROM roles WHERE name = :name";
        List<Roles> results = execute(false, template ->
                template.query(sql, Map.of("name", name), (rs, rowNum) -> authDbMapper.roleFromResultSet(rs)));
        return results.stream().findFirst();
    }

    @Override
    public List<Roles> findAll() {
        String sql = "SELECT * FROM roles";
        return execute(false, template -> template.query(sql, (rs, rowNum) -> authDbMapper.roleFromResultSet(rs)));
    }

    @Override
    public Roles update(Roles role) {
        String sql = "UPDATE roles SET description = :description WHERE id = :id";
        execute(true, template -> template.update(sql, getParamSource(role)));
        return role;
    }

    @Override
    public boolean deleteById(UUID id) {
        String sql = "DELETE FROM roles WHERE id = :id";
        int rows = execute(true, template ->
                template.update(sql, Map.of("id", id)));
        return rows > 0;
    }

    @Override
    public boolean existsById(UUID id) {
        return recordExistsById("roles", id);
    }

    @Override
    public UUID findOrCreateRole(String name) {
        return findByName(name)
                .map(Roles::getId)
                .orElseGet(() -> save(Roles.builder().name(RoleNames.valueOf(name)).description("Автоматически создана").build()).getId());
    }

}
