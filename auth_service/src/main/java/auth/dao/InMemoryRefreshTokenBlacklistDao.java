package auth.dao;

import auth.entity.RefreshTokenBlacklist;
import com.shared.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("refreshTokenBlacklistDao")
public class InMemoryRefreshTokenBlacklistDao implements IRefreshTokenBlacklistDao {

    private final Map<Long, RefreshTokenBlacklist> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public RefreshTokenBlacklist save(RefreshTokenBlacklist entry) {
        Long newId = idGenerator.getAndIncrement();
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
    public Optional<RefreshTokenBlacklist> findById(Long id) {
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
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
}