package review.exception;

import java.util.UUID;

public class VectorStoreNotFoundException extends RuntimeException {
    public VectorStoreNotFoundException(String message) {
        super(message);
    }
    public VectorStoreNotFoundException(UUID id) {
        super("Review with id " + id + " not found");
    }
    public static VectorStoreNotFoundException byReviewId(UUID reviewId) {
        return new VectorStoreNotFoundException("векторное представление для отзыва " +reviewId + "не найдено");
    }
}
