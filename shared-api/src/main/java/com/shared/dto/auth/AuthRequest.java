package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthRequest(
        @JsonProperty("username") String username,
        @JsonProperty("password") String password,
        @JsonProperty("email") String email
) {}