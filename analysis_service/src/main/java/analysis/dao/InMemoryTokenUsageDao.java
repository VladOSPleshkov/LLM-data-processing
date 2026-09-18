package analysis.dao;

import analysis.entity.TokenUsage;
import com.shared.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository("tokenUsageDao")
public class InMemoryTokenUsageDao implements ITokenUsageDao {

    private final Map<Long, TokenUsage> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public TokenUsage save(TokenUsage usage) {
        Long newId = idGenerator.getAndIncrement();
        TokenUsage saved = TokenUsage.builder()
                .id(newId)
                .tasksId(usage.getTasksId())
                .promptTokens(usage.getPromptTokens())
                .responseTokens(usage.getResponseTokens())
                .modelName(usage.getModelName())
                .recorderedAt(usage.getRecorderedAt())
                .build();
        storage.put(newId, saved);
        return saved;
    }

    @Override
    public Optional<TokenUsage> findById(Long id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public List<TokenUsage> findAll() { return new ArrayList<>(storage.values()); }

    @Override
    public List<TokenUsage> findByTasksId(Long tasksId) {
        return storage.values().stream()
                .filter(u -> u.getTasksId().equals(tasksId))
                .collect(Collectors.toList());
    }

    @Override
    public TokenUsage update(TokenUsage usage) {
        if (!storage.containsKey(usage.getId())) {
            throw new EntityNotFoundException("Запись расхода токенов с ID " + usage.getId() + " не найдена");
        }
        storage.put(usage.getId(), usage);
        return usage;
    }

    @Override
    public boolean deleteById(Long id) { return storage.remove(id) != null; }
}
