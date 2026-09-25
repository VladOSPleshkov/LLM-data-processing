package com.shared.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReviewFullResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("brand_id") UUID brandId,
        @JsonProperty("brand_name") String brandName,
        @JsonProperty("content") String content,
        @JsonProperty("rating") Integer rating,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("indexed_at") LocalDateTime indexedAt,
        @JsonProperty("author_name") String authorName,
        @JsonProperty("source") String source,
        @JsonProperty("categories") List<String> categories,
        @JsonProperty("embedding") float[] embedding
) {}

