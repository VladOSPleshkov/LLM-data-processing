package auth.dao;

import auth.entity.User;
import com.shared.dao.AbstractJdbcDao;
import com.shared.exception.DuplicateEntityException;
import com.shared.exception.EntityNotFoundException;
import com.shared.tx.ConnectionHolder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

import java.util.concurrent.ConcurrentHashMap;

@Repository("userDao")
public class JdbcUserDao extends AbstractJdbcDao implements IUserDao {

    private static final String DB_KEY = "AUTH_DB";
    private final AuthDbMapper authDbMapper;

    public JdbcUserDao(ConnectionHolder connectionHolder, AuthDbMapper authDbMapper) {
        super(connectionHolder, DB_KEY);
        this.authDbMapper = authDbMapper;
    }
    private MapSqlParameterSource getParamSource(User user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId().toString())
                .addValue("passwordHash", user.getPasswordHash())
                .addValue("username", user.getUsername())
                .addValue("email", user.getEmail())
                .addValue("registeredAt", user.getRegisteredAt());
    }

    @Override
    public User save(User user) {
        UUID id = user.getId() != null ? user.getId() : UUID.randomUUID();
        User toPersist = user.toBuilder().id(id).build();

        String sql = """
                INSERT INTO users (id, password_hash, username, email, registered_at)
                VALUES (:id, :passwordHash, :username, :email, :registeredAt)
                """;
        execute(true, template -> template.update(sql, getParamSource(toPersist)));
        return toPersist;
    }

    @Override
    public Optional<User> findById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        List<User> results = execute(false, template ->
                template.query(sql, Map.of("id", id.toString()), (rs, rowNum) -> authDbMapper.userFromResultSet(rs)));
        return results.stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = :username";
        List<User> results = execute(false, template ->
                template.query(sql, Map.of("username", username), (rs, rowNum) -> authDbMapper.userFromResultSet(rs)));
        return results.stream().findFirst();
    }

    @Override
    public Optional<User> findByPasswordHash(String passwordHash) {
        String sql = "SELECT * FROM users WHERE password_hash = :paswwordHash";
        List<User> results = execute(false, template ->
                template.query(sql, Map.of("passwordHash", passwordHash), (rs, rowNum) -> authDbMapper.userFromResultSet(rs)));
        return results.stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return execute(false, template ->
                template.query(sql, (rs, rowNum) -> authDbMapper.userFromResultSet(rs)));
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = :email WHERE id = :id";
        execute(true, template -> template.update(sql, getParamSource(user)));
        return user;
    }

    @Override
    public boolean deleteById(UUID id) {
        String sql = "DELETE FROM users WHERE id = :id";
        int rows = execute(true, template -> template.update(sql, Map.of("id", id.toString())));
        return rows > 0;
    }

    @Override
    public boolean existsById(UUID id) {
        return recordExistsById("users", id);
    }
}