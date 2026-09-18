package com.shared.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCategoryRequest(
        @JsonProperty("name") String name
) {}
