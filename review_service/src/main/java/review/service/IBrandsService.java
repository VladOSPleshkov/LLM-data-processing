package review.service;

import com.shared.dto.review.BrandDto;
import com.shared.dto.review.CreateBrandRequest;
import com.shared.dto.review.UpdateBrandRequest;
import java.util.List;
import java.util.UUID;

public interface IBrandsService {
    BrandDto createBrand(CreateBrandRequest request);
    BrandDto getBrandById(UUID id);
    List<BrandDto> getAllBrands();
    BrandDto updateBrand(UUID id, UpdateBrandRequest request);
    boolean deleteBrand(UUID id);
}