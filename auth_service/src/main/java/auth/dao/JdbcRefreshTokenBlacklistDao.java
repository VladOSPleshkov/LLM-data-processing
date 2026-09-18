package auth.dao;

import auth.entity.RefreshTokenBlacklist;
import com.shared.dao.AbstractJdbcDao;
import com.shared.exception.EntityNotFoundException;
import com.shared.tx.ConnectionHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository("refreshTokenBlacklistDao")
public class JdbcRefreshTokenBlacklistDao extends AbstractJdbcDao implements IRefreshTokenBlacklistDao {

    private final Map<UUID, RefreshTokenBlacklist> storage = new ConcurrentHashMap<>();

    protected JdbcRefreshTokenBlacklistDao(ConnectionHolder connectionHolder, String dbKey) {
        super(connectionHolder, dbKey);
    }

    @Override
    public RefreshTokenBlacklist save(RefreshTokenBlacklist entry) {
        UUID newId = UUID.randomUUID();
        RefreshTokenBlacklist saved = RefreshTokenBlacklist.builder()
                .id(newId)
                .token(entry.getToken())
                .userId(entry.getUserId())
                .blacklistedAt(entry.getBlacklistedAt())
                .expiresAt(entry.getExpiresAt())
                .build();
        storage.put(newId, saved);
        return saved;
    }

    @Override
    public Optional<RefreshTokenBlacklist> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<RefreshTokenBlacklist> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public RefreshTokenBlacklist update(RefreshTokenBlacklist entry) {
        if (!storage.containsKey(entry.getId())) {
            throw new EntityNotFoundException("Запись blacklist c ID " + entry.getId() + " не найдена");
        }
        storage.put(entry.getId(), entry);
        return entry;
    }

    @Override
    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(UUID id) {
        return recordExistsById("refresh_token_blacklist", id);
    }
}