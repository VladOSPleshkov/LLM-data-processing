package review.service;

import com.shared.dto.review.CreateReviewRequest;
import com.shared.dto.review.ReviewImportRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import review.dao.BrandsDao;
import review.entity.Brands;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewImportService {

    private final ReviewService reviewService;
    private final BrandsDao brandsDao;

    @Transactional("reviewTransactionManager")
    public int bulkProcess(List<ReviewImportRow> rows) {
        int count = 0;
        Map<String, Brands> localBrandCache = new HashMap<>();

        for (ReviewImportRow row : rows) {
            Brands brand = localBrandCache.computeIfAbsent(row.brandName(), name ->
                    brandsDao.findByName(name).orElseGet(() ->
                            brandsDao.save(Brands.builder()
                                    .brandName(name)
                                    .description("Создан автоматически при импорте")
                                    .createdAt(LocalDateTime.now())
                                    .build())
                    )
            );

            CreateReviewRequest createRequest = new CreateReviewRequest(
                    brand.getId(),
                    row.content(),
                    row.rating(),
                    row.authorName(),
                    row.source(),
                    row.categoryNames()
            );

            reviewService.createReview(createRequest);
            count++;
        }
        log.info("Массовый импорт завершен. Записано отзывов: {}", count);
        return count;
    }
}