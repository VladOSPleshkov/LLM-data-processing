package com.shared.dto.auth;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateRoleRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description
) {}