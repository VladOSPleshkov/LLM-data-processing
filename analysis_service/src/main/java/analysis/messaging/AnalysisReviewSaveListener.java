package analysis.messaging;

import com.shared.dto.analysis.ReviewAnalysisEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisReviewSaveListener {

    private final VectorStore springAiVectorStore;

    @KafkaListener(
            topics = "review-processing-topic",
            groupId = "ai-vectorization-group"
    )
    public void handleAndVectorizeReview(ReviewAnalysisEvent event) {
        log.info("Kafka подхватила отзыв {} для векторизации в базе знаний", event.reviewId());

        try {
            String textToVectorize = String.format(
                    "Бренд: %s. Категория: %s. Отзыв: %s",
                    event.brandName(), event.categoryName(), event.text()
            );

            // Сохраняем важные бизнес-идентификаторы в метаданные для последующей точечной фильтрации ИИ
            Map<String, Object> metadata = Map.of(
                    "reviewId", event.reviewId().toString(),
                    "brandName", event.brandName(),
                    "categoryName", event.categoryName()
            );

            // Создаем абстракцию документа Spring AI
            Document document = new Document(textToVectorize, metadata);

            // Магия Spring AI стартера: автоматически вызовется EmbeddingModel,
            // сгенерирует float[] embedding и выполнит SQL-вставку в pgvector
            springAiVectorStore.add(List.of(document));

            log.info("Отзыв {} успешно векторизован и сохранен в базу знаний ИИ", event.reviewId());

        } catch (Exception e) {
            log.error("Критический сбой векторизации отзыва {}: {}", event.reviewId(), e.getMessage());
        }
    }
}