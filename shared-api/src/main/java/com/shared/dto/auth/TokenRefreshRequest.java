package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank(message = "Refresh-токен обязателен для перевыпуска сессии")
        @JsonProperty("refresh_token") String refreshToken
) {}