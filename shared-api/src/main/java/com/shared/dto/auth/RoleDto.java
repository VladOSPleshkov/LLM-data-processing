package com.shared.dto.auth;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoleDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("created_at")LocalDateTime createdAt
) {}
