package analysis.dao;

import analysis.entity.Tasks;
import com.shared.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("tasksDao")
public class InMemoryTasksDao implements ITasksDao {

    private final Map<Long, Tasks> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Tasks save(Tasks task) {
        Long newId = idGenerator.getAndIncrement();
        Tasks saved = Tasks.builder()
                .id(newId)
                .brandId(task.getBrandId())
                .brandName(task.getBrandName())
                .query(task.getQuery())
                .status(task.getStatus())
                .result(task.getResult())
                .createdAt(task.getCreatedAt())
                .completedAt(task.getCompletedAt())
                .errorMessage(task.getErrorMessage())
                .updatedAt(task.getUpdatedAt())
                .topK(task.getTopK())
                .tokenUsed(task.getTokenUsed())
                .build();
        storage.put(newId, saved);
        return saved;
    }

    @Override
    public Optional<Tasks> findById(Long id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public List<Tasks> findAll() { return new ArrayList<>(storage.values()); }

    @Override
    public Tasks update(Tasks task) {
        if (!storage.containsKey(task.getId())) {
            throw new EntityNotFoundException("Задача с ID " + task.getId() + " не найдена для обновления");
        }
        storage.put(task.getId(), task);
        return task;
    }

    @Override
    public boolean deleteById(Long id) { return storage.remove(id) != null; }
}
