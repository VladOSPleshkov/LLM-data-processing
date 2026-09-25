package com.shared.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public record TokenUsageDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("tasks_id") UUID tasksId,
        @JsonProperty("prompt_tokens") Long promptTokens,
        @JsonProperty("response_tokens") Long responseTokens,
        @JsonProperty("model_name") String modelName,
        @JsonProperty("recordered_at") LocalDateTime recorderedAt
) {}