package review.dao;
import com.shared.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import review.entity.VectorStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("vectorStoreDao")
public class InMemoryVectorStoreDao implements IVectorStoreDao {

    private final Map<UUID, VectorStore> storage = new ConcurrentHashMap<>();

    @Override
    public VectorStore save(VectorStore vector) {
        UUID newId = UUID.randomUUID();
        VectorStore saved = VectorStore.builder()
                .id(newId)
                .metadata(vector.getMetadata())
                .embedding(vector.getEmbedding())
                .reviewsId(vector.getReviewsId())
                .build();
        storage.put(newId, saved);
        return saved;
    }

    @Override
    public Optional<VectorStore> findById(UUID id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public Optional<VectorStore> findByReviewId(UUID reviewId) {
        return storage.values().stream().filter(v -> v.getReviewsId().equals(reviewId)).findFirst();
    }

    @Override
    public List<VectorStore> findAll() { return new ArrayList<>(storage.values()); }

    @Override
    public VectorStore update(VectorStore vector) {
        if (!storage.containsKey(vector.getId())) {
            throw new EntityNotFoundException("Вектор с ID " + vector.getId() + " не найден для обновления");
        }
        storage.put(vector.getId(), vector);
        return vector;
    }

    @Override
    public boolean deleteById(UUID id) { return storage.remove(id) != null; }

    @Override
    public void deleteByReviewId(UUID reviewId) {
        storage.values().removeIf(v -> v.getReviewsId().equals(reviewId));
    }
}