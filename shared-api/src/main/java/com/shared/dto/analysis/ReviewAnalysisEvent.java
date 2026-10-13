package com.shared.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record ReviewAnalysisEvent(
        @JsonProperty("review_id") UUID reviewId,
        @JsonProperty("text") String text,
        @JsonProperty("category_name") String categoryName,
        @JsonProperty("brand_name") String brandName
) {}