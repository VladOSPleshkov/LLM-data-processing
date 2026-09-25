package review.dao;

import com.shared.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import review.entity.Categories;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("categoriesDao")
public class InMemoryCategoriesDao implements ICategoriesDao {

    private final Map<UUID, Categories> storage = new ConcurrentHashMap<>();

    @Override
    public Categories save(Categories category) {
        UUID newId = UUID.randomUUID();
        Categories saved = Categories.builder()
                .id(newId)
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .reviews(category.getReviews())
                .build();
        storage.put(newId, saved);
        return saved;
    }

    @Override
    public Optional<Categories> findById(UUID id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public Optional<Categories> findByName(String name) {
        return storage.values().stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public List<Categories> findAll() { return new ArrayList<>(storage.values()); }

    @Override
    public Categories update(Categories category) {
        if (!storage.containsKey(category.getId())) {
            throw new EntityNotFoundException("Категория с ID " + category.getId() + " не найдена для обновления");
        }
        storage.put(category.getId(), category);
        return category;
    }

    @Override
    public boolean deleteById(UUID id) { return storage.remove(id) != null; }

    @Override
    public UUID findOrCreateCategory(String name) {
        return findByName(name)
                .map(Categories::getId)
                .orElseGet(() -> save(Categories.builder()
                        .name(name)
                        .createdAt(java.time.LocalDateTime.now())
                        .build()).getId());
    }
}