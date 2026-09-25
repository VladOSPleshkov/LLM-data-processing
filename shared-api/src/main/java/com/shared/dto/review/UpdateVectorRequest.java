package com.shared.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateVectorRequest(
        @JsonProperty("embedding") float[] embedding,
        @JsonProperty("metadata") String metadata
) {}
