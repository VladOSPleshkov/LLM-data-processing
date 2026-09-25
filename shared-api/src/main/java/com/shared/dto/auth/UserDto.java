package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("password_hash") String passwordHash,
        @JsonProperty("registered_at") LocalDateTime registeredAt,
        @JsonProperty("roles") List<UserRoleInfoDto> roles
) {}
