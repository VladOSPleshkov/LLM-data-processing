package com.shared.dto.analysis;
import java.time.LocalDateTime;
import java.util.UUID;

public record AnalysisCompletedEvent(
        UUID taskId,
        UUID brandId,
        //String brandName,
        String status,
        String result,
        String errorMessage,
        LocalDateTime completedAt
) {}
