package analysis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)

public class TokenUsage {
    private final UUID id;
    private final UUID tasksId;
    private final Long promptTokens;
    private final Long responseTokens;
    private final String modelName;
    private final LocalDateTime recorderedAt;

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
