package com.shared.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record RemoveCategoryFromReviewRequest(
        @JsonProperty("review_id") UUID reviewId,
        @JsonProperty("category_id") UUID categoryId
) {}