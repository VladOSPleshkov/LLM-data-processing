package auth.service;

import auth.dao.TokenBlacklistDao;
import auth.dao.TokenBlacklistDao;
import auth.entity.RefreshTokenBlacklist;
import com.shared.dto.auth.CreateBlacklistTokenRequest;
import com.shared.dto.auth.RefreshTokenBlacklistDto;
import com.shared.dto.auth.UpdateBlacklistTokenRequest;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.ITokenBlacklistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("tokenBlacklistService")
public class TokenBlacklistService implements ITokenBlacklistService {

    private final TokenBlacklistDao tokenBlacklistDao;

    public TokenBlacklistService(TokenBlacklistDao tokenBlacklistDao) {
        this.tokenBlacklistDao = tokenBlacklistDao;
    }

    @Transactional
    public RefreshTokenBlacklistDto blacklistToken(CreateBlacklistTokenRequest request) {
        RefreshTokenBlacklist blacklistEntry = RefreshTokenBlacklist.builder()
                .userId(request.userId())
                .token(request.token())
                .revokedAt(request.revokedAt() != null ? request.revokedAt() : LocalDateTime.now())
                .expiresAt(request.expiresAt())
                .revokeReason(request.revokeReason())
                .build();

        RefreshTokenBlacklist saved = tokenBlacklistDao.save(blacklistEntry);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public RefreshTokenBlacklistDto getById(UUID id) {
        RefreshTokenBlacklist entity = tokenBlacklistDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись блэклиста с ID " + id + " не найдена"));
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistDao.isBlacklisted(token);
    }

    @Transactional(readOnly = true)
    public List<RefreshTokenBlacklistDto> getByUserId(UUID userId) {
        return tokenBlacklistDao.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RefreshTokenBlacklistDto> getAll() {
        return tokenBlacklistDao.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RefreshTokenBlacklistDto update(UUID id, UpdateBlacklistTokenRequest request) {
        RefreshTokenBlacklist entity = tokenBlacklistDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись блэклиста с ID " + id + " не найдена"));

        entity.setExpiresAt(request.expiresAt());
        return toDto(entity);
    }

    @Transactional
    public boolean delete(UUID id) {
        return tokenBlacklistDao.deleteById(id);
    }

    @Transactional
    public int purgeExpiredTokens() {
        return tokenBlacklistDao.deleteExpiredTokens(LocalDateTime.now());
    }

    private RefreshTokenBlacklistDto toDto(RefreshTokenBlacklist entity) {
        return new RefreshTokenBlacklistDto(
                entity.getUserId(),
                entity.getToken(),
                entity.getRevokedAt(),
                entity.getExpiresAt()
        );
    }
}