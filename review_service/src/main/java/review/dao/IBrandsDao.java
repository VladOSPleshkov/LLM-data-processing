package review.dao;

import review.entity.Brands;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBrandsDao {
    Brands save(Brands brand);
    Optional<Brands> findById(UUID id);
    List<Brands> findAll();
    Brands update(Brands brand);
    boolean deleteById(UUID id);
}