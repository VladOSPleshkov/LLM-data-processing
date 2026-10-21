package com.shared.dto.review;

import java.time.LocalDateTime;
import java.util.UUID;

public record AnalysisReportDto(
        UUID taskId, UUID brandId, String brandName, String status,
        String result, String errorMessage,
        LocalDateTime completedAt, LocalDateTime receivedAt, boolean acknowledged
) {}
