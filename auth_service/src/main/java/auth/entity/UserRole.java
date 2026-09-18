package auth.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserRole {
    private final UUID userId;
    private final RoleNames role;         // Ссылка на сам объект Role
    private final LocalDateTime assignedAt;
    private final UUID assignedBy;    // FK на того, кто выдал роль

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole userRole)) return false;
        return Objects.equals(userId, userRole.userId) &&
                Objects.equals(role != null ? role.getId() : null, userRole.role != null ? userRole.role.getId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, role != null ? role.getId() : null);
    }

}
