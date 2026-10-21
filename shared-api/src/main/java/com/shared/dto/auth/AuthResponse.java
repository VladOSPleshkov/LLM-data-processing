package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AuthResponse(
        @JsonProperty("user_id")
        UUID userId,

        @JsonProperty("username")
        String username,

        @JsonProperty("roles")
        List<String> roles
) {}