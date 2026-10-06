package analysis.exception;

import com.shared.exception.EntityNotFoundException;

import java.util.UUID;

public class TaskNotFoundException extends EntityNotFoundException {
    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(UUID id) {
        super("Задача с ID " + id + " не найдена");
    }
}