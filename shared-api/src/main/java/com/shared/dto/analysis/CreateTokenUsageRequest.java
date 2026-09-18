package com.shared.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateTokenUsageRequest(
        @JsonProperty("tasks_id") UUID tasksId,
        @JsonProperty("prompt_tokens") Long promptTokens,
        @JsonProperty("response_tokens") Long responseTokens,
        @JsonProperty("model_name") String modelName
) {}
