package com.shared.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtAuthResponse(@JsonProperty("token") String token ) {}
