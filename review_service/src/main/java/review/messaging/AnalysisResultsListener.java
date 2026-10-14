package review.messaging;
import com.shared.dto.analysis.AnalysisCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import review.dao.AnalysisReportDao;
import review.entity.AnalysisReport;
import review.exception.BrandsNotFoundException;

import java.time.LocalDateTime;
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisResultsListener {

    private final AnalysisReportDao analysisReportDao;
    private final review.dao.BrandsDao brandsDao; // Внедряем локальный DAO брендов

    @KafkaListener(topics = "analysis-results", groupId = "review-analysis-results-group")
    @Transactional("reviewTransactionManager")
    public void handleAnalysisCompleted(AnalysisCompletedEvent event) {
        log.info("Получен результат анализа из Kafka. Задача: {}, Статус: {}", event.taskId(), event.status());

        // ЗАЩИТА: Находим локальный бренд в review_db. Если его нет — честная 404 ошибка вместо 500
        review.entity.Brands brand = brandsDao.findById(event.brandId())
                .orElseThrow(() -> new BrandsNotFoundException(event.brandId()));

        AnalysisReport report = analysisReportDao.findById(event.taskId())
                .map(existing -> {
                    existing.setStatus(event.status());
                    existing.setResult(event.result());
                    existing.setErrorMessage(event.errorMessage());
                    existing.setCompletedAt(event.completedAt());
                    return existing;
                })
                .orElseGet(() -> {
                    AnalysisReport newReport = AnalysisReport.builder()
                            .taskId(event.taskId())
                            .brand(brand)
                            .status(event.status())
                            .result(event.result())
                            .errorMessage(event.errorMessage())
                            .completedAt(event.completedAt())
                            .receivedAt(LocalDateTime.now())
                            .acknowledged(false)
                            .build();
                    return newReport;
                });

        analysisReportDao.save(report);
        log.info("Отчёт по задаче {} успешно синхронизирован в 3НФ", event.taskId());
    }
}
