package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UpdateUserRequest(
        @JsonProperty("email") String email,
        @JsonProperty("role_names") List<String> roleNames
) {}