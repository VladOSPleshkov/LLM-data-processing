package analysis.dao;

import analysis.entity.TokenUsage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITokenUsageDao {
    TokenUsage save(TokenUsage usage);
    Optional<TokenUsage> findById(UUID id);
    List<TokenUsage> findAll();
    List<TokenUsage> findByTasksId(UUID tasksId);
    TokenUsage update(TokenUsage usage);
    boolean deleteById(UUID id);
    boolean existsById(UUID id);
}
