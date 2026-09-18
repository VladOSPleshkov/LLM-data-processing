package review.dao;

import review.entity.Categories;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICategoriesDao {
    Categories save(Categories category);
    Optional<Categories> findById(UUID id);
    Optional<Categories> findByName(String name);
    List<Categories> findAll();
    Categories update(Categories category);
    boolean deleteById(UUID id);
    UUID findOrCreateCategory(String name);
}