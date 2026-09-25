package auth.dao;

import auth.entity.RefreshTokenBlacklist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRefreshTokenBlacklistDao {
    RefreshTokenBlacklist save(RefreshTokenBlacklist entry);
    Optional<RefreshTokenBlacklist> findById(UUID id);
    List<RefreshTokenBlacklist> findAll();
    RefreshTokenBlacklist update(RefreshTokenBlacklist entry);
    boolean deleteById(UUID id);
    boolean existsById(UUID id);
}
