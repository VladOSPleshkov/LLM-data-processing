package review.dao;

import com.shared.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import review.entity.Reviews;

import java.util.*;


import java.util.concurrent.ConcurrentHashMap;

@Repository("reviewDao")
public class InMemoryReviewDao implements IReviewDao {

    private final Map<UUID, Reviews> databaseTable = new ConcurrentHashMap<>();

    @Override
    public Reviews create(Reviews review) {
        UUID newId = UUID.randomUUID();
        Reviews savedReview = Reviews.builder()
                .id(newId)
                .brandId(review.getBrandId())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .indexedAt(review.getIndexedAt())
                .authorName(review.getAuthorName())
                .source(review.getSource())
                .categories(review.getCategories())
                .build();
        databaseTable.put(newId, savedReview);
        return savedReview;
    }

    @Override
    public Optional<Reviews> readById(UUID id) { return Optional.ofNullable(databaseTable.get(id)); }

    @Override
    public List<Reviews> readAll() { return new ArrayList<>(databaseTable.values()); }

    @Override
    public Reviews update(Reviews review) {
        if (!databaseTable.containsKey(review.getId())) {
            throw new EntityNotFoundException("Отзыв с ID " + review.getId() + " не найден для обновления");
        }
        databaseTable.put(review.getId(), review);
        return review;
    }

    @Override
    public boolean deleteById(UUID id) { return databaseTable.remove(id) != null; }
}