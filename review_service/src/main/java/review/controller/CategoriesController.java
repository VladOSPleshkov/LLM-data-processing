package review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.CrudController;
import com.shared.dto.review.CategoryDto;
import com.shared.dto.review.CreateCategoryRequest;
import com.shared.dto.review.UpdateCategoryRequest;
import com.shared.service.ICategoriesService;
import org.springframework.stereotype.Component;
import com.shared.dto.DeleteStatus;

import java.util.UUID;

@Component("categoriesController")
public class CategoriesController extends AbstractReviewController
        implements CrudController<CreateCategoryRequest, CategoryDto, UpdateCategoryRequest, UUID> {

    private final ICategoriesService categoriesService;

    public CategoriesController(ICategoriesService categoriesService, ObjectMapper objectMapper) {
        super(objectMapper, "categoriesController");
        this.categoriesService = categoriesService;
    }

    @Override
    public String processCreate(CreateCategoryRequest request) { return toJson(categoriesService.createCategory(request)); }

    @Override
    public String processGetById(UUID id) { return toJson(categoriesService.getCategoryById(id)); }

    @Override
    public String processGetAll() { return toJson(categoriesService.getAllCategories()); }

    @Override
    public String processUpdate(UUID id, UpdateCategoryRequest request) { return toJson(categoriesService.updateCategory(id, request)); }

    @Override
    public String processDelete(UUID id) {
        boolean deleted = categoriesService.deleteCategory(id);
        return toJson(new DeleteStatus(deleted, "Категория с ID " + id + " обработана"));
    }

    @Override
    public String execute() { return "categoriesController активен"; }

}