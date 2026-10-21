package com.shared.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ReviewImportRow(
        @JsonProperty("brand_name") String brandName,
        @JsonProperty("category_names") List<String> categoryNames,
        @JsonProperty("content") String content,
        @JsonProperty("rating") Integer rating,
        @JsonProperty("author_name") String authorName,
        @JsonProperty("source") String source
) {}
