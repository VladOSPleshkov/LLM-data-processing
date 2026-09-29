package review.service;

import com.shared.dto.review.CategoryDto;
import com.shared.dto.review.CreateCategoryRequest;
import com.shared.dto.review.UpdateCategoryRequest;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.ICategoriesService;
import org.springframework.stereotype.Component;
import review.entity.Categories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.shared.dto.review.CategoryDto;
import com.shared.dto.review.CreateCategoryRequest;
import com.shared.dto.review.UpdateCategoryRequest;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.ICategoriesService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import review.dao.CategoriesDao;
import review.entity.Categories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("categoriesService")
public class CategoriesService implements ICategoriesService {

    private final CategoriesDao categoriesDao;

    public CategoriesService(CategoriesDao categoriesDao) {
        this.categoriesDao = categoriesDao;
    }

    @Override
    @Transactional("reviewTransactionManager")
    public CategoryDto createCategory(CreateCategoryRequest request) {
        Categories entity = Categories.builder()
                .name(request.name())
                .createdAt(LocalDateTime.now())
                .build();
        return mapToDto(categoriesDao.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(UUID id) {
        Categories category = categoriesDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория с ID " + id + " не найдена"));
        return mapToDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        List<CategoryDto> result = new ArrayList<>();
        for (Categories category : categoriesDao.findAll()) {
            result.add(mapToDto(category));
        }
        return result;
    }

    @Override
    @Transactional("reviewTransactionManager")
    public CategoryDto updateCategory(UUID id, UpdateCategoryRequest request) {
        Categories existing = categoriesDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория с ID " + id + " не найдена"));

        if (request.name() != null) {
            existing.setName(request.name());
        }
        return mapToDto(existing);
    }

    @Override
    @Transactional("reviewTransactionManager")
    public boolean deleteCategory(UUID id) {
        if (!categoriesDao.findById(id).isPresent()) {
            return false;
        }
        // Удаление Categories автоматически убирает связанные строки review_categories
        // (join-таблица управляется JPA-провайдером в рамках маппинга ManyToMany).
        categoriesDao.deleteById(id);
        return true;
    }

    private CategoryDto mapToDto(Categories category) {
        int reviewsCount = (int) categoriesDao.countReviewsByCategoryId(category.getId());
        return new CategoryDto(category.getId(), category.getName(), category.getCreatedAt(), reviewsCount);
    }
}