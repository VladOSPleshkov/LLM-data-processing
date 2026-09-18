package com.shared.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TaskWithUsageResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("status") String status,
        @JsonProperty("query") String query,
        @JsonProperty("result") String result,
        @JsonProperty("token_usage_logs") List<TokenUsageDto> tokenUsageLogs,
        @JsonProperty("created_at") LocalDateTime createdAt
) {}