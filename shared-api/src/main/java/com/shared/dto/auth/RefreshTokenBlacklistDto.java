package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public record RefreshTokenBlacklistDto(
        @JsonProperty("user_id") UUID userId,
        @JsonProperty("token") String token,
        @JsonProperty("revoked_at") LocalDateTime revokedAt,
        @JsonProperty("expires_at") LocalDateTime expiresAt
) {}