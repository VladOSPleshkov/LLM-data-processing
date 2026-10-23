package review.service;


import com.shared.dto.review.BrandDto;
import com.shared.dto.review.CreateBrandRequest;
import com.shared.dto.review.UpdateBrandRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import review.dao.BrandsDao;
import review.entity.Brands;
import review.exception.BrandsNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("brandsService")
public class BrandsService implements IBrandsService {

    private final BrandsDao brandsDao;

    public BrandsService(BrandsDao brandsDao) {
        this.brandsDao = brandsDao;
    }

    @Override
    @Transactional("reviewTransactionManager")
    public BrandDto createBrand(CreateBrandRequest request) {
        Brands entity = Brands.builder()
                .brandName(request.brandName())
                .description(request.description())
                .createdAt(LocalDateTime.now())
                .build();
        return mapToDto(brandsDao.save(entity));
    }

    @Override
    @Transactional(value = "reviewTransactionManager",readOnly = true)
    public BrandDto getBrandById(UUID id) {
        Brands brand = brandsDao.findById(id)
                .orElseThrow(() -> new BrandsNotFoundException(id));
        return mapToDto(brand);
    }

    @Override
    @Transactional(value = "reviewTransactionManager", readOnly = true)
    public List<BrandDto> getAllBrands() {
        List<BrandDto> result = new ArrayList<>();
        for (Brands brand : brandsDao.findAll()) {
            result.add(mapToDto(brand));
        }
        return result;
    }

    @Override
    @Transactional("reviewTransactionManager")
    public BrandDto updateBrand(UUID id, UpdateBrandRequest request) {
        Brands existing = brandsDao.findById(id)
                .orElseThrow(() -> new BrandsNotFoundException(id));

        if (request.description() != null) {
            existing.setDescription(request.description());
        }

        return mapToDto(existing);
    }

    @Override
    @Transactional("reviewTransactionManager")
    public boolean deleteBrand(UUID id) {
        if (brandsDao.findById(id).isEmpty()) {
            return false;
        }
        brandsDao.deleteById(id);
        return true;
    }

    private BrandDto mapToDto(Brands brand) {
        long reviewsCount = brandsDao.countReviewsByBrandId(brand.getId());
        return new BrandDto(
                brand.getId(),
                brand.getBrandName(),
                brand.getDescription(),
                brand.getCreatedAt(),
                (int) reviewsCount
        );
    }
}