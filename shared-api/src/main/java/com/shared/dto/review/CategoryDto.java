package com.shared.dto.review;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("reviews_count") int reviewsCount
) {}
