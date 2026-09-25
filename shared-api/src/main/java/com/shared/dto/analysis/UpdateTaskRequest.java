package com.shared.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateTaskRequest(
        @JsonProperty("status") String status,
        @JsonProperty("result") String result,
        @JsonProperty("error_message") String errorMessage,
        @JsonProperty("token_used") Integer tokenUsed
) {}
