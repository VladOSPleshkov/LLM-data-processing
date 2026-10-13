package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Имя пользователя не может быть пустым")
        @Size(min = 4, max = 50, message = "Имя пользователя должно быть от 4 до 50 символов")
        @JsonProperty("username")
        String username,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 6, max = 100, message = "Пароль должен быть не менее 6 символов")
        @JsonProperty("password")
        String password,

        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
        @JsonProperty("email")
        String email
) {}