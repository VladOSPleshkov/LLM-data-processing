package analysis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "token_usages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TokenUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tasks_id", nullable = false)
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
        if (o == null || getClass() != o.getClass()) return false;
        TokenUsage usage = (TokenUsage) o;
        return id != null && id.equals(usage.id);
    }

    @Override
    public int hashCode() { return TokenUsage.class.hashCode(); }
}
