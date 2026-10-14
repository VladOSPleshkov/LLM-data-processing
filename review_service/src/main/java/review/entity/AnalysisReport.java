package review.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "analysis_reports")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisReport {

    @Id
    @Column(name = "task_id")
    private UUID taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brands brand;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String result;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(nullable = false)
    private boolean acknowledged;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brands that)) return false;
        return getTaskId() != null && Objects.equals(getTaskId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}
