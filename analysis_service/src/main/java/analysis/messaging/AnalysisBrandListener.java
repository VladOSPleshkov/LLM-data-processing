package analysis.messaging;

import analysis.service.TasksService;
import com.shared.dto.analysis.CreateTaskRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AnalysisBrandListener {

    private final TasksService analysisProcessor;

    @KafkaListener(topics = "analysis-requests", groupId = "analysis-group")
    public void handleAnalysisRequest(CreateTaskRequest request) {
        log.info("Получено событие на анализ бренда: {}", request.brandName());
        try {
            analysisProcessor.startAnalysis(request);
        } catch (Exception e) {
            log.error("Критическая ошибка при отправке задачи на процессинг", e);
        }
    }
}
