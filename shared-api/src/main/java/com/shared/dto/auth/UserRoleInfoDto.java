package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

// Отражает промежуточную сущность UserRole (assigned_at/assigned_by) в ответах api
public record UserRoleInfoDto(
        @JsonProperty("role_id") UUID roleId,
        @JsonProperty("role_name") String roleName,
        @JsonProperty("assigned_at") LocalDateTime assignedAt,
        @JsonProperty("assigned_by") UUID assignedBy
) {}
