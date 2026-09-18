package com.shared.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("brand_id") UUID brandId,
        @JsonProperty("brand_name") String brandName,
        @JsonProperty("query") String query,
        @JsonProperty("status") String status,
        @JsonProperty("result") String result,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("completed_at") LocalDateTime completedAt,
        @JsonProperty("error_message") String errorMessage,
        @JsonProperty("updated_at") LocalDateTime updatedAt,
        @JsonProperty("top_k") int topK,
        @JsonProperty("token_used") int tokenUsed
) {}
