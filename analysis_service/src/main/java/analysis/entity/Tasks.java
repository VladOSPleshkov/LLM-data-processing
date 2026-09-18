package analysis.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Tasks {
    private UUID id;
    private UUID brandId;
    private String brandName;
    private String query;
    private Status status;
    private String result;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    private LocalDateTime updatedAt;
    private int topK;
    private int tokenUsed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tasks tasks = (Tasks) o;
        return id != null && id.equals(tasks.id);
    }
    @Override
    public int hashCode() { return Tasks.class.hashCode(); }
}
