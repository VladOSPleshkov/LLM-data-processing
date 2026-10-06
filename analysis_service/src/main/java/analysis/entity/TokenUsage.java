package analysis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "token_usage")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Tasks task;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "prompt_tokens")
    private Long promptTokens;

    @Column(name = "response_tokens")
    private Long responseTokens;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenUsage that)) return false;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
