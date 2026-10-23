package analysis.controller;

import com.shared.dto.analysis.CreateTaskRequest;
import com.shared.dto.analysis.TaskDto;
import com.shared.dto.analysis.UpdateTaskRequest;
import analysis.service.ITasksService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
public class TasksController {

    private final ITasksService tasksService;

    public TasksController(ITasksService tasksService) {
        this.tasksService = tasksService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok(tasksService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable UUID id) {
        return ResponseEntity.ok(tasksService.getTaskById(id));
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tasksService.createTask(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable UUID id, @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(tasksService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        tasksService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/start-analysis")
    public ResponseEntity<TaskDto> startAnalysis(@RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tasksService.startAnalysis(request));
    }
}