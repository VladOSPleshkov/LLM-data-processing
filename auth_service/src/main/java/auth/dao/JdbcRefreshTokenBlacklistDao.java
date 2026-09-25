package auth.dao;

import auth.entity.RefreshTokenBlacklist;
import com.shared.dao.AbstractJdbcDao;
import com.shared.exception.EntityNotFoundException;
import com.shared.tx.ConnectionHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.Types;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository("refreshTokenBlacklistDao")
public class JdbcRefreshTokenBlacklistDao extends AbstractJdbcDao implements IRefreshTokenBlacklistDao {

    private static final String DB_KEY = "AUTH_DB";
    private final AuthDbMapper authDbMapper;

    public JdbcRefreshTokenBlacklistDao(ConnectionHolder connectionHolder, AuthDbMapper authDbMapper) {
        super(connectionHolder, DB_KEY);
        this.authDbMapper = authDbMapper;
    }

    private MapSqlParameterSource getParamSource(RefreshTokenBlacklist entry) {
        return new MapSqlParameterSource()
                .addValue("id", entry.getId().toString(), Types.OTHER)
                .addValue("token", entry.getToken())
                .addValue("userId", entry.getUserId() != null ? entry.getUserId().toString() : null)
                .addValue("blacklistedAt", entry.getBlacklistedAt())
                .addValue("expiresAt", entry.getExpiresAt());
    }

    @Override
    public RefreshTokenBlacklist save(RefreshTokenBlacklist entry) {
        UUID id = entry.getId() != null ? entry.getId() : UUID.randomUUID();
        RefreshTokenBlacklist toPersist = entry.toBuilder().id(id).build();

        String sql = """
            INSERT INTO refresh_token_blacklist (id, token, user_id, blacklisted_at, expires_at)
            VALUES (:id, :token, :userId, :blacklistedAt, :expiresAt)
            """;

        execute(true, template -> template.update(sql, getParamSource(toPersist)));
        return toPersist;
    }

    @Override
    public Optional<RefreshTokenBlacklist> findById(UUID id) {
        String sql = "SELECT * FROM refresh_token_blacklist WHERE id = :id";
        List<RefreshTokenBlacklist> results = execute(false, template ->
                template.query(sql, Map.of("id", id.toString()), (rs, rowNum) -> authDbMapper.blacklistFromResultSet(rs))
        );
        return results.stream().findFirst();
    }

    @Override
    public List<RefreshTokenBlacklist> findAll() {
        String sql = "SELECT * FROM refresh_token_blacklist";
        return execute(false, template ->
                template.query(sql, (rs, rowNum) -> authDbMapper.blacklistFromResultSet(rs))
        );
    }

    @Override
    public RefreshTokenBlacklist update(RefreshTokenBlacklist entry) {
        if (!existsById(entry.getId())) {
            throw new EntityNotFoundException("Запись blacklist c ID " + entry.getId() + " не найдена");
        }

        String sql = """
            UPDATE refresh_token_blacklist
            SET token = :token, user_id = :userId, blacklisted_at = :blacklistedAt, expires_at = :expiresAt
            WHERE id = :id
            """;

        execute(true, template -> template.update(sql, getParamSource(entry)));
        return entry;
    }

    @Override
    public boolean deleteById(UUID id) {
        String sql = "DELETE FROM refresh_token_blacklist WHERE id = :id";
        int rows = execute(true, template -> template.update(sql, Map.of("id", id.toString())));
        return rows > 0;
    }

    @Override
    public boolean existsById(UUID id) {
        return recordExistsById("refresh_token_blacklist", id);
    }
}