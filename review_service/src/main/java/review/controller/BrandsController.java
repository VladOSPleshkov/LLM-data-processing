package review.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.CrudController;
import com.shared.dto.review.BrandDto;
import com.shared.dto.review.CreateBrandRequest;
import com.shared.dto.review.UpdateBrandRequest;
import com.shared.service.IBrandsService;
import org.springframework.stereotype.Component;
import com.shared.dto.DeleteStatus;

import java.util.UUID;

@Component("brandsController")
public class BrandsController extends AbstractReviewController
        implements CrudController<CreateBrandRequest, BrandDto, UpdateBrandRequest, UUID> {

    private final IBrandsService brandsService;

    public BrandsController(IBrandsService brandsService, ObjectMapper objectMapper) {
        super(objectMapper, "brandsController");
        this.brandsService = brandsService;
    }

    @Override
    public String processCreate(CreateBrandRequest request) { return toJson(brandsService.createBrand(request)); }

    @Override
    public String processGetById(UUID id) { return toJson(brandsService.getBrandById(id)); }

    @Override
    public String processGetAll() { return toJson(brandsService.getAllBrands()); }

    @Override
    public String processUpdate(UUID id, UpdateBrandRequest request) { return toJson(brandsService.updateBrand(id, request)); }

    @Override
    public String processDelete(UUID id) {
        boolean deleted = brandsService.deleteBrand(id);
        return toJson(new DeleteStatus(deleted, "Бренд с ID " + id + " обработан"));
    }

    @Override
    public String execute() { return "brandsController активен"; }

}
