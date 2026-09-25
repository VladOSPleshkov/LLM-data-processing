package analysis.service;

import analysis.dao.ITasksDao;
import analysis.dao.ITokenUsageDao;
import analysis.entity.Status;
import analysis.entity.Tasks;
import analysis.entity.TokenUsage;
import com.shared.dto.analysis.*;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.ITasksService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.shared.common.annotation.ManualTransactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TasksService implements ITasksService {

    private final ITasksDao tasksDao;
    private final ITokenUsageDao tokenUsageDao;
    private final TasksMapper mapper;
    private static final String DB_KEY = "ANALYSIS_DB";

    public TasksService(ITasksDao tasksDao, ITokenUsageDao tokenUsageDao, TasksMapper mapper) {
        this.tasksDao = tasksDao;
        this.tokenUsageDao = tokenUsageDao;
        this.mapper = mapper;
    }
    @Override
    public TaskDto createTask(CreateTaskRequest request) {
        // Одна запись в одну таблицу формальная транзакция не нужна
        // вместо неё явная проверка успеха
        Tasks entity = mapper.toEntity(request);
        Tasks saved = tasksDao.save(entity);

        if (!tasksDao.existsById(saved.getId())) {
            throw new IllegalStateException("Не удалось подтвердить сохранение задачи " + saved.getId());
        }
        return mapper.toDto(saved);
    }
    @Override
    public TaskDto getTaskById(UUID id) {
        Tasks task = tasksDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача с ID " + id + " не найдена"));
        return mapper.toDto(task);
    }

    @Override
    public List<TaskDto> getAllTasks() {
        return mapper.toDtoList(tasksDao.findAll());
    }

    @Override
    public TaskDto updateTask(UUID id, UpdateTaskRequest request) {
        Tasks existing = tasksDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача с ID " + id + " не найдена"));

        mapper.updateEntityFromRequest(request, existing);
        Tasks result = tasksDao.update(existing);

        if (!tasksDao.existsById(id)) {
            throw new IllegalStateException("Не удалось подтвердить обновление задачи " + id);
        }
        return mapper.toDto(result);
    }

    @Override
    public boolean deleteTask(UUID id) {
        // Каскад: сначала логи токенов, потом сама задача — согласованность
        // важна, поэтому оборачиваем в транзакцию.
        return deleteTaskTransactional(id);
    }

    @ManualTransactional(dbKey = DB_KEY)
    protected boolean deleteTaskTransactional(UUID id) {
        tokenUsageDao.findByTasksId(id).forEach(usage -> tokenUsageDao.deleteById(usage.getId()));
        return tasksDao.deleteById(id);
    }

    /**
     * Многошаговая операция (1 задача + 2 записи расхода токенов) — должна быть
     * атомарной, поэтому выполняется в рамках единой ручной транзакции.
     */
    @Override
    @ManualTransactional(dbKey = DB_KEY)
    public TaskWithUsageResponse startAnalysis(CreateTaskRequest request) {
        Tasks task = Tasks.builder()
                .brandId(request.brandId())
                .brandName(request.brandName())
                .query(request.query())
                .status(Status.PROCESSING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .topK(request.topK())
                .tokenUsed(0)
                .build();
        Tasks savedTask = tasksDao.save(task);
        UUID taskId = savedTask.getId();

        tokenUsageDao.save(TokenUsage.builder()
                .tasksId(taskId).modelName("gpt-4o-mini")
                .promptTokens(150L).responseTokens(45L)
                .recorderedAt(LocalDateTime.now()).build());

        tokenUsageDao.save(TokenUsage.builder()
                .tasksId(taskId).modelName("gpt-4o")
                .promptTokens(1200L).responseTokens(450L)
                .recorderedAt(LocalDateTime.now()).build());

        int totalTokens = 150 + 45 + 1200 + 450;
        String mockLlmResult = "Анализ завершён. 85% отзывов позитивны.";

        Tasks completedTask = savedTask.toBuilder()
                .status(Status.COMPLETED)
                .result(mockLlmResult)
                .completedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .tokenUsed(totalTokens)
                .build();
        tasksDao.update(completedTask);

        return buildUsageResponse(completedTask);
    }

    @Override
    public TaskWithUsageResponse getTaskWithUsages(UUID id) {
        Tasks task = tasksDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача с ID " + id + " не найдена"));
        return buildUsageResponse(task);
    }

    private TaskWithUsageResponse buildUsageResponse(Tasks task) {
        List<TokenUsageDto> logs = tokenUsageDao.findByTasksId(task.getId()).stream()
                .map(u -> new TokenUsageDto(u.getId(), u.getTasksId(), u.getPromptTokens(),
                        u.getResponseTokens(), u.getModelName(), u.getRecorderedAt()))
                .collect(Collectors.toList());

        return new TaskWithUsageResponse(
                task.getId(), task.getStatus().name(), task.getQuery(),
                task.getResult(), logs, task.getCreatedAt()
        );
    }
}