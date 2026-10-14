package com.shared.service;
import com.shared.dto.auth.CreateBlacklistTokenRequest;
import com.shared.dto.auth.RefreshTokenBlacklistDto;
import com.shared.dto.auth.UpdateBlacklistTokenRequest;
import java.util.List;
import java.util.UUID;

public interface ITokenBlacklistService {
    RefreshTokenBlacklistDto blacklistToken(CreateBlacklistTokenRequest request);
    RefreshTokenBlacklistDto getById(UUID id);
    List<RefreshTokenBlacklistDto> getAll();
    RefreshTokenBlacklistDto update(UUID id, UpdateBlacklistTokenRequest request);
    boolean delete(UUID id);
    boolean isTokenBlacklisted(String token);
}
