package com.shared.service;
import com.shared.dto.review.CategoryDto;
import com.shared.dto.review.CreateCategoryRequest;
import com.shared.dto.review.UpdateCategoryRequest;
import java.util.List;
import java.util.UUID;

public interface ICategoriesService {
    CategoryDto createCategory(CreateCategoryRequest request);
    CategoryDto getCategoryById(UUID id);
    List<CategoryDto> getAllCategories();
    CategoryDto updateCategory(UUID id, UpdateCategoryRequest request);
    boolean deleteCategory(UUID id);
}
