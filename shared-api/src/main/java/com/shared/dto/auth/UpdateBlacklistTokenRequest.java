package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record UpdateBlacklistTokenRequest(
        @JsonProperty("expires_at") LocalDateTime expiresAt
) {}