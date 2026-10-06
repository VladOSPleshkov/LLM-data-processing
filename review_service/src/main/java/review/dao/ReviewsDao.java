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

@Repository
public class ReviewsDao extends AbstractReviewDao<Reviews, UUID> {

    @PersistenceContext(unitName = "reviewUnit")
    protected EntityManager entityManager;
    public ReviewsDao() {
        super(Reviews.class);
    }

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
