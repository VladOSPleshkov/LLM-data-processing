package review.dao;

import com.shared.dao.AbstractDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import review.entity.Reviews;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO отзывов. Основной сценарий выборки (getById/getAll) собирает
 * brand + categories + vectorStore ЗА ОДИН SQL-запрос через LEFT JOIN FETCH,
 * что полностью устраняет проблему N+1, присутствовавшую в legacy-версии
 * (там категории и вектор дотягивались по отдельным DAO-вызовам в цикле).
 */
@Repository
public class ReviewsDao extends AbstractDao<Reviews, UUID> {

    @PersistenceContext(unitName = "reviewUnit")
    protected EntityManager entityManager;
    public ReviewsDao() {
        super(Reviews.class);
    }

    /** Одиночная выборка со всеми ленивыми ассоциациями за 1 запрос. */
    public Optional<Reviews> findByIdWithDetails(UUID id) {
        String jpql = """
                SELECT DISTINCT r
                FROM Reviews r
                LEFT JOIN FETCH r.brand
                LEFT JOIN FETCH r.categories
                LEFT JOIN FETCH r.vectorStore
                WHERE r.id = :id
                """;
        TypedQuery<Reviews> query = entityManager.createQuery(jpql, Reviews.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    /** Массовая выборка со всеми ленивыми ассоциациями за 1 запрос. */
    public List<Reviews> findAllWithDetails() {
        String jpql = """
                SELECT DISTINCT r
                FROM Reviews r
                LEFT JOIN FETCH r.brand
                LEFT JOIN FETCH r.categories
                LEFT JOIN FETCH r.vectorStore
                """;
        return entityManager.createQuery(jpql, Reviews.class).getResultList();
    }

    public List<Reviews> findAllByBrandId(UUID brandId) {
        String jpql = """
                SELECT DISTINCT r
                FROM Reviews r
                LEFT JOIN FETCH r.categories
                WHERE r.brand.id = :brandId
                """;
        TypedQuery<Reviews> query = entityManager.createQuery(jpql, Reviews.class);
        query.setParameter("brandId", brandId);
        return query.getResultList();
    }
}
