package com.shared.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateTokenUsageRequest(
        @JsonProperty("prompt_tokens") Long promptTokens,
        @JsonProperty("response_tokens") Long responseTokens
) {}
