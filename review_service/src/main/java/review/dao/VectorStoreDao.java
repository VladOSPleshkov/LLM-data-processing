package review.dao;

import com.shared.dao.AbstractDao;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import review.entity.VectorStore;

import java.util.Optional;
import java.util.UUID;

@Repository
public class VectorStoreDao extends AbstractDao<VectorStore, UUID> {

    public VectorStoreDao() {
        super(VectorStore.class);
    }

    public Optional<VectorStore> findByReviewId(UUID reviewId) {
        TypedQuery<VectorStore> query = entityManager.createQuery(
                "SELECT v FROM VectorStore v WHERE v.review.id = :reviewId", VectorStore.class);
        query.setParameter("reviewId", reviewId);
        return query.getResultStream().findFirst();
    }

    public void deleteByReviewId(UUID reviewId) {
        findByReviewId(reviewId).ifPresent(entityManager::remove);
    }
}
