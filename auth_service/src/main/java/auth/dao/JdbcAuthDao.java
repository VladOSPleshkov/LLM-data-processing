package auth.dao;

import com.shared.dao.AbstractJdbcDao;
import com.shared.tx.ConnectionHolder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("authDao")
public class JdbcAuthDao extends AbstractJdbcDao implements AuthDao {

    private static final String DB_KEY = "AUTH_DB";
    private final AuthDbMapper authDbMapper;

    public JdbcAuthDao(ConnectionHolder connectionHolder, AuthDbMapper authDbMapper) {
        super(connectionHolder, DB_KEY);
        this.authDbMapper = authDbMapper;
    }

    @Override
    public UUID saveUser(String username, String passwordHash) {
        // Проверяем, существует ли пользователь
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = :username";
        Integer count = execute(false, template ->
                template.queryForObject(checkSql, Map.of("username", username), Integer.class)
        );

        if (count != null && count > 0) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        UUID newId = UUID.randomUUID();
        String sql = """
            INSERT INTO users (id, username, password_hash)
            VALUES (:id, :username, :passwordHash)
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", newId.toString())
                .addValue("username", username)
                .addValue("passwordHash", passwordHash);

        execute(true, template -> template.update(sql, params));
        return newId;
    }

    @Override
    public Optional<String> findPasswordHashByUsername(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = :username";
        List<String> results = execute(false, template ->
                template.query(sql, Map.of("username", username), (rs, rowNum) -> rs.getString("password_hash"))
        );
        return results.stream().findFirst();
    }

    @Override
    public Optional<UUID> findIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = :username";
        List<UUID> results = execute(false, template ->
                template.query(sql, Map.of("username", username), (rs, rowNum) -> UUID.fromString(rs.getString("id")))
        );
        return results.stream().findFirst();
    }
}