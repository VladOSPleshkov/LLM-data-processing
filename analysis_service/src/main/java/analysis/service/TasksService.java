package analysis.service;

import analysis.dao.TasksDao;
import analysis.entity.Status;
import analysis.entity.Tasks;
import analysis.entity.TokenUsage;
import com.shared.dto.analysis.*;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.ITasksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service("tasksService")
public class TasksService implements ITasksService {

    private final TasksDao tasksDao;
    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public TaskDto createTask(CreateTaskRequest request) {
        return startAnalysis(request);
    }

    @Override
    @Transactional
    public TaskDto startAnalysis(CreateTaskRequest request) {
        Tasks task = Tasks.builder()
                .brandId(request.brandId())
                .brandName(request.brandName())
                .query(request.query())
                .status(Status.PROCESSING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .topK(request.topK() > 0 ? request.topK() : 10)
                .tokenUsed(0)
                .build();
        tasksDao.save(task);

        try {
            FilterExpressionBuilder fb = new FilterExpressionBuilder();
            var filterExpression = fb.eq("brandName", task.getBrandName()).build();

            SearchRequest searchRequest = SearchRequest.query(task.getQuery())
                    .withTopK(task.getTopK())
                    .withFilterExpression(filterExpression);

            List<Document> relevantDocs = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(task.getQuery())
                            .topK(task.getTopK())
                            .similarityThreshold(0.6)
                            .filterExpression("brandId == '" + task.getBrandId() + "'")
                            .build()
            );

            // 2. Формирование реального contextReviews
            String contextReviews = relevantDocs.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n---\n"));

            if (contextReviews.isBlank()) {
                contextReviews = "Отзывы по данному бренду в базе не найдены.";
            }

            // 3. Вызов LLM через ChatClient
            ChatClient chatClient = chatClientBuilder.build();
            String promptText = String.format(
                    """
                    Инструкция: Проанализируй следующие отзывы для бренда %s.
                    Запрос пользователя: %s
    
                    Контекст (найденные отзывы):
                    %s
                    """,
                    task.getBrandName(), task.getQuery(), contextReviews
            );

            ChatResponse response = chatClient.prompt()
                    .user(promptText)
                    .call()
                    .chatResponse();

            String llmResult = response.getResult().getOutput().getText();
            Usage usage = response.getMetadata().getUsage();

            long promptTokens = usage != null ? usage.getPromptTokens() : 0;
            long generationTokens = usage != null ? usage.getCompletionTokens() : 0;
            String modelName = response.getMetadata().getModel() != null
                    ? response.getMetadata().getModel() : "llama-3.3-70b";

            TokenUsage tokenUsage = TokenUsage.builder()
                    .task(task)
                    .modelName(modelName)
                    .promptTokens(promptTokens)
                    .responseTokens(generationTokens)
                    .recordedAt(LocalDateTime.now())
                    .build();

            task.getTokenUsages().add(tokenUsage);
            task.setResult(llmResult);
            task.setStatus(Status.COMPLETED);
            task.setTokenUsed((int) (promptTokens + generationTokens));
            task.setCompletedAt(LocalDateTime.now());


        } catch (Exception ex) {
            log.error("Ошибка при работе с LLM для задачи {}", task.getId(), ex);
            task.setStatus(Status.FAILED);
            task.setErrorMessage(ex.getMessage());
        } finally {
            task.setUpdatedAt(LocalDateTime.now());
            tasksDao.save(task);

            // 5. Отправляем событие о завершении анализа в Kafka для других микросервисов
            kafkaTemplate.send("analysis-results", task.getId().toString(), task.getResult());
        }
        return toDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskById(UUID id) {
        Tasks task = tasksDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача с ID " + id + " не найдена"));
        return toDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskWithUsages(UUID id) {
        // Подгружает задачу и TokenUsage за 1 SQL-запрос через LEFT JOIN FETCH
        Tasks task = tasksDao.findByIdWithTokenUsages(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача с ID " + id + " не найдена"));
        return toDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks() {
        return tasksDao.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskDto updateTask(UUID id, UpdateTaskRequest request) {
        Tasks task = tasksDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача с ID " + id + " не найдена"));

        if (request.status() != null) {
            task.setStatus(Status.valueOf(request.status()));
        }
        if (request.result() != null) {
            task.setResult(request.result());
        }
        if (request.errorMessage() != null) {
            task.setErrorMessage(request.errorMessage());
        }
        if (request.tokenUsed() != null) {
            task.setTokenUsed(request.tokenUsed());
        }

        task.setUpdatedAt(LocalDateTime.now());

        return toDto(task);
    }

    @Override
    public boolean deleteTask(UUID id) {
        if (!tasksDao.deleteById(id)) {
            throw new EntityNotFoundException("Задача с ID " + id + " не найдена для удаления");
        } else {
            return tasksDao.deleteById(id);
        }
    }

    private TokenUsageDto toTokenUsageDto(TokenUsage tu) {
        return new TokenUsageDto(
                tu.getId(),
                tu.getTask() != null ? tu.getTask().getId() : null,
                tu.getPromptTokens(),
                tu.getResponseTokens(),
                tu.getModelName(),
                tu.getRecordedAt()
        );
    }

    private TaskDto toDto(Tasks task) {
        List<TokenUsageDto> tokenUsageDtos = task.getTokenUsages() == null ? List.of() :
                task.getTokenUsages().stream()
                        .map(this::toTokenUsageDto)
                        .collect(Collectors.toList());

        return new TaskDto(
                task.getId(),
                task.getBrandId(),
                task.getBrandName(),
                task.getQuery(),
                task.getStatus() != null ? task.getStatus().name() : null,
                task.getResult(),
                task.getCreatedAt(),
                task.getCompletedAt(),
                task.getErrorMessage(),
                task.getUpdatedAt(),
                task.getTopK(),
                task.getTokenUsed()
                //tokenUsageDtos
        );
    }
}