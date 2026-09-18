package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record RevokeRoleRequest(
        @JsonProperty("user_id") UUID userId,
        @JsonProperty("role_id") UUID roleId
) {}
