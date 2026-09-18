package com.shared.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record AnalysisTaskResponse(
        @JsonProperty("task_id") Long taskId,
        @JsonProperty("status") String status, // IN_PROGRESS, COMPLETED
        @JsonProperty("instruction") String instruction,
        @JsonProperty("llm_result") String llmResult, // Текст ответа от нейросети
        @JsonProperty("token_logs") List<TokenUsageDto> tokenLogs, // Связанная таблица token_usage
        @JsonProperty("created_at") LocalDateTime createdAt
) {}
//что с таблицей