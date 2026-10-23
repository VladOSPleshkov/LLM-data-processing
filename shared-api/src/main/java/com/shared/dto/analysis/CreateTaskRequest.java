package com.shared.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateTaskRequest(
        //@JsonProperty("brand_id") UUID brandId,
        @JsonProperty("brand_name") String brandName,
        @JsonProperty("query") String query,
        @JsonProperty("top_k") int topK
) {}
