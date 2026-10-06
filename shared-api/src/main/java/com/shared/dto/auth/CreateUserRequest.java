package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CreateUserRequest(
        @JsonProperty("username") String username,
        @JsonProperty("password") String passwordHash,
        @JsonProperty("email") String email,
        @JsonProperty("role_names") List<String> roleNames
) {}