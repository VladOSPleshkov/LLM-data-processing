package review.service;

import com.shared.dto.analysis.ReviewAnalysisEvent;
import com.shared.dto.review.CreateReviewRequest;
import com.shared.dto.review.ReviewFullResponse;
import com.shared.dto.review.ReviewImportRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import review.dao.BrandsDao;
import review.entity.Brands;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewImportService {

    private final ReviewService reviewService;
    private final BrandsDao brandsDao;
    //private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public int bulkProcess(List<ReviewImportRow> rows) {
        int count = 0;
        for (ReviewImportRow row : rows) {
            Brands brand = brandsDao.findByName(row.brandName()).orElseGet(() ->
                    brandsDao.save(Brands.builder()
                            .brandName(row.brandName())
                            .description("Создан автоматически при импорте")
                            .createdAt(LocalDateTime.now())
                            .build())
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
        log.info("Массовый импорт завершен. Записано отзывов и отправлено в Kafka: {}", count);
        return count;
    }
}
