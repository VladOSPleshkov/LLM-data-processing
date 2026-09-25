package review.dao;

import java.util.List;
import java.util.UUID;

/* Mock join-таблицы для many-to-many Reviews <-> Categories. */
public interface IReviewCategoryDao {
    void link(UUID reviewId, UUID categoryId);
    void unlink(UUID reviewId, UUID categoryId);
    List<UUID> findCategoryIdsByReviewId(UUID reviewId);
    List<UUID> findReviewIdsByCategoryId(UUID categoryId);
    void deleteAllByReviewId(UUID reviewId);
    void deleteAllByCategoryId(UUID categoryId);
}