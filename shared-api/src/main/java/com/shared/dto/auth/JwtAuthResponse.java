package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtAuthResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken) {}
