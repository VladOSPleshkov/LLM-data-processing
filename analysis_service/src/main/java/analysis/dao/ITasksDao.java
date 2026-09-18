package analysis.dao;

import analysis.entity.Tasks;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITasksDao {
    Tasks save(Tasks task);
    Optional<Tasks> findById(UUID id);
    List<Tasks> findAll();
    Tasks update(Tasks task);
    boolean deleteById(UUID id);
    boolean existsById(UUID id);
}
