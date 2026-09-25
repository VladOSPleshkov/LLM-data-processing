package com.shared.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UpdateReviewRequest(
        @JsonProperty("content") String content,
        @JsonProperty("rating") Integer rating,
        @JsonProperty("category_names") List<String> categoryNames
) {}
