package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateBlacklistTokenRequest(
        @JsonProperty("revoke_reason") String revokeReason,
        @JsonProperty("user_id") UUID userId,
        @JsonProperty("expires_at") LocalDateTime expiresAt
) {}
