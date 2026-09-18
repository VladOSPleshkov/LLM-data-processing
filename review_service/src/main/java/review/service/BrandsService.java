package review.service;


import com.shared.dto.review.BrandDto;
import com.shared.dto.review.CreateBrandRequest;
import com.shared.dto.review.UpdateBrandRequest;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IBrandsService;
import org.springframework.stereotype.Component;
import review.dao.IBrandsDao;
import review.dao.IReviewDao;
import review.entity.Brands;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("brandsService")
public class BrandsService implements IBrandsService {

    private final IBrandsDao brandsDao;
    private final IReviewDao reviewDao;

    public BrandsService(IBrandsDao brandsDao, IReviewDao reviewDao) {
        this.brandsDao = brandsDao;
        this.reviewDao = reviewDao;
    }

    @Override
    public BrandDto createBrand(CreateBrandRequest request) {
        Brands entity = Brands.builder()
                .brandName(request.brandName())
                .description(request.description())
                .createdAt(LocalDateTime.now())
                .build();
        return mapToDto(brandsDao.save(entity));
    }

    @Override
    public BrandDto getBrandById(UUID id) {
        Brands brand = brandsDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Бренд с ID " + id + " не найден"));
        return mapToDto(brand);
    }

    @Override
    public List<BrandDto> getAllBrands() {
        List<BrandDto> result = new ArrayList<>();
        for (Brands brand : brandsDao.findAll()) result.add(mapToDto(brand));
        return result;
    }

    @Override
    public BrandDto updateBrand(UUID id, UpdateBrandRequest request) {
        Brands existing = brandsDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Бренд с ID " + id + " не найден"));

        Brands updated = Brands.builder()
                .id(existing.getId())
                .brandName(existing.getBrandName())
                .description(request.description() != null ? request.description() : existing.getDescription())
                .createdAt(existing.getCreatedAt())
                .build();

        brandsDao.update(updated);
        return mapToDto(updated);
    }

    @Override
    public boolean deleteBrand(UUID id) { return brandsDao.deleteById(id); }

    private BrandDto mapToDto(Brands brand) {
        long reviewsCount = reviewDao.readAll().stream()
                .filter(r -> r.getBrandId().equals(brand.getId()))
                .count();
        return new BrandDto(brand.getId(), brand.getBrandName(), brand.getDescription(),
                brand.getCreatedAt(), (int) reviewsCount);
    }
}