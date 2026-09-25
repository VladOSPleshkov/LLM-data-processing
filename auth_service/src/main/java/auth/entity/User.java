package auth.entity;
import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private UUID id;
    private String passwordHash;
    private String username;
    private String email;
    private LocalDateTime registeredAt;

    // Связь идет через промежуточную сущность, чтобы не потерять assigned_at и assigned_by
    private Set<UserRole> userRoles = new HashSet<>();
    public void revokeRole(Roles role) {
        if (this.userRoles != null) {
            this.userRoles.removeIf(ur -> ur.getRole().equals(role));
        }

        role.getUsers().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getEmail() != null && Objects.equals(getEmail(), user.email);
        //уникальный ключ по email
    }

    @Override
    public int hashCode() { return User.class.hashCode(); }

    public void assignRole(Roles role, UUID id) {

    }
}