package analysis.dao;

import analysis.entity.Status;
import analysis.entity.Tasks;
import com.shared.dao.AbstractDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class TasksDao extends AbstractDao<Tasks, UUID> {

    @PersistenceContext(unitName = "analysisUnit")
    protected EntityManager entityManager;


    public TasksDao() {
        super(Tasks.class);
    }

    // Загрузка задачи вместе со связанными TokenUsage в один запрос через JPQL
    public Optional<Tasks> findByIdWithTokenUsages(UUID id) {
        String jpql = "SELECT DISTINCT t FROM Tasks t LEFT JOIN FETCH t.tokenUsages WHERE t.id = :id";
        return entityManager.createQuery(jpql, Tasks.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    // Получение всех задач по идентификатору бренда
    public List<Tasks> findByBrandId(UUID brandId) {
        String jpql = "SELECT t FROM Tasks t WHERE t.brandId = :brandId ORDER BY t.createdAt DESC";
        return entityManager.createQuery(jpql, Tasks.class)
                .setParameter("brandId", brandId)
                .getResultList();
    }

    // Получение задач по статусу
    public List<Tasks> findByStatus(Status status) {
        String jpql = "SELECT t FROM Tasks t WHERE t.status = :status ORDER BY t.createdAt DESC";
        return entityManager.createQuery(jpql, Tasks.class)
                .setParameter("status", status)
                .getResultList();
    }
}