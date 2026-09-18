package analysis.service;


import analysis.dao.ITasksDao;
import analysis.dao.ITokenUsageDao;
import analysis.entity.TokenUsage;
import com.shared.dto.analysis.CreateTokenUsageRequest;
import com.shared.dto.analysis.TokenUsageDto;
import com.shared.dto.analysis.UpdateTokenUsageRequest;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.ITokenUsageService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("tokenUsageService")
public class TokenUsageService implements ITokenUsageService {

    private final ITokenUsageDao tokenUsageDao;
    private final ITasksDao tasksDao;

    public TokenUsageService(ITokenUsageDao tokenUsageDao, ITasksDao tasksDao) {
        this.tokenUsageDao = tokenUsageDao;
        this.tasksDao = tasksDao;
    }

    @Override
    public TokenUsageDto createUsage(CreateTokenUsageRequest request) {
        tasksDao.findById(request.tasksId())
                .orElseThrow(() -> new EntityNotFoundException("Задача с ID " + request.tasksId() + " не найдена"));

        TokenUsage entity = TokenUsage.builder()
                .tasksId(request.tasksId())
                .promptTokens(request.promptTokens())
                .responseTokens(request.responseTokens())
                .modelName(request.modelName())
                .recorderedAt(LocalDateTime.now())
                .build();

        return mapToDto(tokenUsageDao.save(entity));
    }

    @Override
    public TokenUsageDto getUsageById(Long id) {
        return mapToDto(tokenUsageDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись расхода токенов с ID " + id + " не найдена")));
    }

    @Override
    public List<TokenUsageDto> getAllUsage() {
        List<TokenUsageDto> result = new ArrayList<>();
        for (TokenUsage usage : tokenUsageDao.findAll()) result.add(mapToDto(usage));
        return result;
    }

    @Override
    public List<TokenUsageDto> getUsageByTaskId(Long taskId) {
        return tokenUsageDao.findByTasksId(taskId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public TokenUsageDto updateUsage(Long id, UpdateTokenUsageRequest request) {
        TokenUsage existing = tokenUsageDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись расхода токенов с ID " + id + " не найдена"));

        TokenUsage updated = TokenUsage.builder()
                .id(existing.getId())
                .tasksId(existing.getTasksId())
                .promptTokens(request.promptTokens() != null ? request.promptTokens() : existing.getPromptTokens())
                .responseTokens(request.responseTokens() != null ? request.responseTokens() : existing.getResponseTokens())
                .modelName(existing.getModelName())
                .recorderedAt(existing.getRecorderedAt())
                .build();

        tokenUsageDao.update(updated);
        return mapToDto(updated);
    }

    @Override
    public boolean deleteUsage(Long id) { return tokenUsageDao.deleteById(id); }

    private TokenUsageDto mapToDto(TokenUsage usage) {
        return new TokenUsageDto(
                usage.getId(), usage.getTasksId(), usage.getPromptTokens(),
                usage.getResponseTokens(), usage.getModelName(), usage.getRecorderedAt()
        );
    }
}