package analysis.messaging;

import com.shared.dto.analysis.ReviewAnalysisEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewIngestionListener {

    private final VectorStore vectorStore;

    @KafkaListener(topics = "review-processing-topic", groupId = "analysis-group")
    public void handleReviewIngested(ReviewAnalysisEvent event) {
        log.info("Получен отзыв для бренда {} на векторизацию", event.brandName());

        Document rawDoc = new Document(
                event.text(),
                Map.of(
                        "reviewId", event.reviewId().toString(),
                        "brandName", event.brandName(),
                        "category", event.categoryName() != null ? event.categoryName() : ""
                )
        );

        // Чанкинг токенов
        TokenTextSplitter splitter = new TokenTextSplitter(800, 350, 5, 1000, true);
        List<Document> chunks = splitter.apply(List.of(rawDoc));

        // Векторизация и сохранение в pgvector (analysis_db)
        vectorStore.add(chunks);
        log.info("Отзыв {} успешно сохранен в pgvector", event.reviewId());
    }
}

