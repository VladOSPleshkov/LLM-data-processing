package review.dao;

import review.entity.VectorStore;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IVectorStoreDao {
    VectorStore save(VectorStore vector);
    Optional<VectorStore> findById(UUID id);
    Optional<VectorStore> findByReviewId(UUID reviewId);
    List<VectorStore> findAll();
    VectorStore update(VectorStore vector);
    boolean deleteById(UUID id);
    void deleteByReviewId(UUID reviewId);
}