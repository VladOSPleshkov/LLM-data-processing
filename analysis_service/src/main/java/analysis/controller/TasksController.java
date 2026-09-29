package analysis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.CrudController;
import com.shared.dto.DeleteStatus;
import com.shared.dto.analysis.CreateTaskRequest;
import com.shared.dto.analysis.TaskDto;
import com.shared.dto.analysis.UpdateTaskRequest;
import com.shared.service.ITasksService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("tasksController")
public class TasksController implements CrudController<CreateTaskRequest, TaskDto, UpdateTaskRequest, UUID> {

    private final ITasksService tasksService;
    private final ObjectMapper objectMapper;

    @Value("${ANALYSIS_MODULE_NAME:analysis_service}")
    private String moduleName;

    public TasksController(ITasksService tasksService, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.tasksService = tasksService;
    }

    @Override
    public String processCreate(CreateTaskRequest request) {
        return toJson(tasksService.createTask(request));
    }

    @Override
    public String processGetById(UUID id) {
        return toJson(tasksService.getTaskById(id));
    }

    @Override
    public String processGetAll() {
        return toJson(tasksService.getAllTasks());
    }

    @Override
    public String processUpdate(UUID id, UpdateTaskRequest request) {
        return toJson(tasksService.updateTask(id, request));
    }

    @Override
    public String processDelete(UUID id) {
        boolean deleted = tasksService.deleteTask(id);
        return toJson(new DeleteStatus(deleted, "Задача с ID " + id + " обработана"));
    }

    public String processStartAnalysis(CreateTaskRequest request) {
        return toJson(tasksService.startAnalysis(request));
    }

    /**
     * Эндпоинт для получения задачи со всем списком TokenUsage
     */
    public String processGetWithUsage(UUID id) {
        return toJson(tasksService.getTaskWithUsages(id));
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    public String execute() {
        return moduleName + " активен";
    }

    protected String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка Jackson-сериализации в " + moduleName, e);
        }
    }
}