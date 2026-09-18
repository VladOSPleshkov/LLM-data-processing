package com.shared.dto.review;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public record BrandDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("brand_name") String brandName,
        @JsonProperty("description") String description,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("reviews_count") int reviewsCount
) {}