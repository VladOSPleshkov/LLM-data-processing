package review.service;


import com.shared.dto.review.BrandDto;
import com.shared.dto.review.CreateBrandRequest;
import com.shared.dto.review.UpdateBrandRequest;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IBrandsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import review.dao.BrandsDao;
import review.entity.Brands;

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
    @Transactional
    public BrandDto createBrand(CreateBrandRequest request) {
        Brands entity = Brands.builder()
                .brandName(request.brandName())
                .description(request.description())
                .createdAt(LocalDateTime.now())
                .build();
        return mapToDto(brandsDao.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto getBrandById(UUID id) {
        Brands brand = brandsDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Бренд с ID " + id + " не найден"));
        return mapToDto(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDto> getAllBrands() {
        List<BrandDto> result = new ArrayList<>();
        for (Brands brand : brandsDao.findAll()) {
            result.add(mapToDto(brand));
        }
        return result;
    }

    @Override
    @Transactional
    public BrandDto updateBrand(UUID id, UpdateBrandRequest request) {
        Brands existing = brandsDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Бренд с ID " + id + " не найден"));

        if (request.description() != null) {
            existing.setDescription(request.description());
        }
        // merge не обязателен — сущность управляема (managed) в текущей транзакции,
        // изменения будут сброшены в БД автоматически при коммите (dirty checking).
        return mapToDto(existing);
    }

    @Override
    @Transactional
    public boolean deleteBrand(UUID id) {
        if (!brandsDao.findById(id).isPresent()) {
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