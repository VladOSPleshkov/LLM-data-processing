package com.shared.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateVectorRequest(
        @JsonProperty("reviews_id") UUID reviewsId,
        @JsonProperty("embedding") float[] embedding,
        @JsonProperty("metadata") String metadata
) {}