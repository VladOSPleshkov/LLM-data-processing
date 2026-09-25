package com.shared.dto.review;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record CreateReviewRequest(
        @JsonProperty("brand_id") UUID brandId,
        @JsonProperty("content") String content,
        @JsonProperty("rating") Integer rating,
        @JsonProperty("author_name") String authorName,
        @JsonProperty("source") String source,
        @JsonProperty("category_names") List<String> categoryNames,
        @JsonProperty("embedding") float[] embedding,
        @JsonProperty("metadata") String metadata
) {}