package analysis.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
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
    //private UUID createdBy;

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
