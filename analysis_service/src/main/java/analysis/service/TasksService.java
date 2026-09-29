package analysis.service;

import analysis.dao.TasksDao;
import analysis.entity.Status;
import analysis.entity.Tasks;
import analysis.entity.TokenUsage;
import com.shared.dto.analysis.*;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.ITasksService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("tasksService")
public class TasksService implements ITasksService {

    private final TasksDao tasksDao;

    public TasksService(TasksDao tasksDao) {
        this.tasksDao = tasksDao;
    }

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

        Tasks savedTask = tasksDao.save(task);
        processTaskMock(savedTask); // Имитация обработки
        return toDto(savedTask);
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

        // Dirty Checking Hibernate автоматически выполнит UPDATE при коммите транзакции
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

    private void processTaskMock(Tasks task) {
        String mockResult = "Аналитический отчет по запросу '" + task.getQuery() + "'.";
        long mockPrompt = 150L;
        long mockResponse = 250L;

        TokenUsage usage = TokenUsage.builder()
                .task(task)
                .modelName("mock-llm-v1")
                .promptTokens(mockPrompt)
                .responseTokens(mockResponse)
                .recordedAt(LocalDateTime.now())
                .build();

        task.getTokenUsages().add(usage);
        task.setStatus(Status.COMPLETED);
        task.setResult(mockResult);
        task.setTokenUsed((int) (mockPrompt + mockResponse));
        task.setCompletedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
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