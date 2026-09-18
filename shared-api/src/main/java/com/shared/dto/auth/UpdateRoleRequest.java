package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateRoleRequest(
        @JsonProperty("description") String description
) {}