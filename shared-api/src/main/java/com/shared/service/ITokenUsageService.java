package com.shared.service;

import com.shared.dto.analysis.CreateTokenUsageRequest;
import com.shared.dto.analysis.TokenUsageDto;
import com.shared.dto.analysis.UpdateTokenUsageRequest;
import java.util.List;
import java.util.UUID;

public interface ITokenUsageService {
    TokenUsageDto createUsage(CreateTokenUsageRequest request);
    TokenUsageDto getUsageById(UUID id);
    List<TokenUsageDto> getAllUsage();
    List<TokenUsageDto> getUsageByTaskId(UUID taskId);
    TokenUsageDto updateUsage(UUID id, UpdateTokenUsageRequest request);
    boolean deleteUsage(UUID id);
}