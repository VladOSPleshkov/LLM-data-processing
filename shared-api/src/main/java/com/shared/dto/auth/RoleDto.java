package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record RoleDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description) {}
