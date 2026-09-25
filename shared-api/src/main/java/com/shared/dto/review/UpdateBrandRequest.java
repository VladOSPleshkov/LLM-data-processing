package com.shared.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateBrandRequest(
        @JsonProperty("description") String description
) {}