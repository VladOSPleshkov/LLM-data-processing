package com.shared.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateAnalysisRequest(
        @JsonProperty("module_target") String moduleTarget, // например, "reviews"
        @JsonProperty("prompt_instruction") String promptInstruction
) {
}

