package analysis.service;

import com.shared.dto.analysis.CreateTaskRequest;
import com.shared.dto.analysis.TaskDto;
import com.shared.dto.analysis.UpdateTaskRequest;
import java.util.List;
import java.util.UUID;

public interface ITasksService {
    TaskDto createTask(CreateTaskRequest request);
    TaskDto getTaskById(UUID id);
    List<TaskDto> getAllTasks();
    TaskDto updateTask(UUID id, UpdateTaskRequest request);
    boolean deleteTask(UUID id);

    /*Асинхронное выполнение RAG-конвейера с симуляцией расхода токенов. */
    TaskDto startAnalysis(CreateTaskRequest request);
    TaskDto getTaskWithUsages(UUID id);
}