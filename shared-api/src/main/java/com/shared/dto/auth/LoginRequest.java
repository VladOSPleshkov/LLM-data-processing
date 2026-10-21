package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Имя пользователя обязательно для входа")
        @JsonProperty("username")
        String username,

        @NotBlank(message = "Пароль обязателен для входа")
        @JsonProperty("password")
        String password
) {}
