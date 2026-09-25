package review.dao;

import review.entity.Reviews;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IReviewDao {
    Reviews create(Reviews review);
    Optional<Reviews> readById(UUID id);
    List<Reviews> readAll();
    Reviews update(Reviews review);
    boolean deleteById(UUID id);
}