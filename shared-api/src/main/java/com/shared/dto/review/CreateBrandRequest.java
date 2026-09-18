package com.shared.dto.review;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateBrandRequest(
        @JsonProperty("brand_name") String brandName,
        @JsonProperty("description") String description
) {}