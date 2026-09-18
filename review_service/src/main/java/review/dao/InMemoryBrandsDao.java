package review.dao;

import com.shared.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import review.entity.Brands;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("brandsDao")
public class InMemoryBrandsDao implements IBrandsDao {

    private final Map<UUID, Brands> storage = new ConcurrentHashMap<UUID, Brands>();

    @Override
    public Brands save(Brands brand) {
        UUID newId = UUID.randomUUID();
        Brands saved = Brands.builder()
                .id(newId)
                .brandName(brand.getBrandName())
                .description(brand.getDescription())
                .createdAt(brand.getCreatedAt())
                .build();
        storage.put(newId, saved);
        return saved;
    }

    @Override
    public Optional<Brands> findById(UUID id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public List<Brands> findAll() { return new ArrayList<>(storage.values()); }

    @Override
    public Brands update(Brands brand) {
        if (!storage.containsKey(brand.getId())) {
            throw new EntityNotFoundException("Бренд с ID " + brand.getId() + " не найден для обновления");
        }
        storage.put(brand.getId(), brand);
        return brand;
    }

    @Override
    public boolean deleteById(UUID id) { return storage.remove(id) != null; }
}