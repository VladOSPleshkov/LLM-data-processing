package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AuthRequest(
        @JsonProperty("username") String username,
        @JsonProperty("password_hash") String passwordHash,
        @JsonProperty("email") String email,
        @JsonProperty("roles") List<String> roles
) {}
