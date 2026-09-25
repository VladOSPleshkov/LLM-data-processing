package review.service;

import com.shared.dto.review.CategoryDto;
import com.shared.dto.review.CreateCategoryRequest;
import com.shared.dto.review.UpdateCategoryRequest;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.ICategoriesService;
import org.springframework.stereotype.Component;
import review.dao.ICategoriesDao;
import review.dao.IReviewCategoryDao;
import review.entity.Categories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("categoriesService")
public class CategoriesService implements ICategoriesService {

    private final ICategoriesDao categoriesDao;
    private final IReviewCategoryDao reviewCategoryDao;

    public CategoriesService(ICategoriesDao categoriesDao, IReviewCategoryDao reviewCategoryDao) {
        this.categoriesDao = categoriesDao;
        this.reviewCategoryDao = reviewCategoryDao;
    }

    @Override
    public CategoryDto createCategory(CreateCategoryRequest request) {
        Categories entity = Categories.builder()
                .name(request.name())
                .createdAt(LocalDateTime.now())
                .build();
        return mapToDto(categoriesDao.save(entity));
    }

    @Override
    public CategoryDto getCategoryById(UUID id) {
        Categories category = categoriesDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория с ID " + id + " не найдена"));
        return mapToDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<CategoryDto> result = new ArrayList<>();
        for (Categories category : categoriesDao.findAll()) result.add(mapToDto(category));
        return result;
    }

    @Override
    public CategoryDto updateCategory(UUID id, UpdateCategoryRequest request) {
        Categories existing = categoriesDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория с ID " + id + " не найдена"));

        Categories updated = Categories.builder()
                .id(existing.getId())
                .name(request.name() != null ? request.name() : existing.getName())
                .createdAt(existing.getCreatedAt())
                .reviews(existing.getReviews())
                .build();

        categoriesDao.update(updated);
        return mapToDto(updated);
    }

    @Override
    public boolean deleteCategory(UUID id) {
        reviewCategoryDao.deleteAllByCategoryId(id);
        return categoriesDao.deleteById(id);
    }

    private CategoryDto mapToDto(Categories category) {
        int reviewsCount = reviewCategoryDao.findReviewIdsByCategoryId(category.getId()).size();
        return new CategoryDto(category.getId(), category.getName(), category.getCreatedAt(), reviewsCount);
    }
}