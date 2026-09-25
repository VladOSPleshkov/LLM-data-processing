package auth.service;

import auth.dao.IRefreshTokenBlacklistDao;
import auth.entity.RefreshTokenBlacklist;
import com.shared.dto.auth.CreateBlacklistTokenRequest;
import com.shared.dto.auth.RefreshTokenBlacklistDto;
import com.shared.dto.auth.UpdateBlacklistTokenRequest;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.ITokenBlacklistService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("tokenBlacklistService")
public class TokenBlacklistService implements ITokenBlacklistService {

    private final IRefreshTokenBlacklistDao blacklistDao;

    public TokenBlacklistService(IRefreshTokenBlacklistDao blacklistDao) {
        this.blacklistDao = blacklistDao;
    }

    @Override
    public RefreshTokenBlacklistDto blacklistToken(CreateBlacklistTokenRequest request) {
        RefreshTokenBlacklist entity = RefreshTokenBlacklist.builder()
                .userId(request.userId())
                .blacklistedAt(LocalDateTime.now())
                .expiresAt(request.expiresAt())
                .build();
        return mapToDto(blacklistDao.save(entity));
    }

    @Override
    public RefreshTokenBlacklistDto getById(UUID id) {
        return mapToDto(blacklistDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись blacklist с ID " + id + " не найдена")));
    }

    @Override
    public List<RefreshTokenBlacklistDto> getAll() {
        List<RefreshTokenBlacklistDto> result = new ArrayList<>();
        for (RefreshTokenBlacklist entity : blacklistDao.findAll()) {
            result.add(mapToDto(entity));
        }
        return result;
    }

    @Override
    public RefreshTokenBlacklistDto update(UUID id, UpdateBlacklistTokenRequest request) {
        RefreshTokenBlacklist existing = blacklistDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись blacklist с ID " + id + " не найдена"));

        RefreshTokenBlacklist updated = RefreshTokenBlacklist.builder()
                .id(existing.getId())
                .token(existing.getToken())
                .userId(existing.getUserId())
                .blacklistedAt(existing.getBlacklistedAt())
                .expiresAt(request.expiresAt() != null ? request.expiresAt() : existing.getExpiresAt())
                .build();

        blacklistDao.update(updated);
        return mapToDto(updated);
    }

    @Override
    public boolean delete(UUID id) {
        return blacklistDao.deleteById(id);
    }

    private RefreshTokenBlacklistDto mapToDto(RefreshTokenBlacklist entity) {
        return new RefreshTokenBlacklistDto(
                entity.getId(), entity.getToken(), entity.getUserId(),
                entity.getBlacklistedAt(), entity.getExpiresAt()
        );
    }
}