package auth.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Roles {
    private UUID id;
    private RoleNames name;
    @Setter
    private String description;
    @Builder.Default
    private Set<User> users = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Roles roles = (Roles) o;
        return id != null && Objects.equals(id, roles.id);
    }

    @Override
    public int hashCode() {
        return Roles.class.hashCode();
    }

    public void addUser(User savedUser) {
        if (savedUser != null) {
            this.users.add(savedUser);
        }
    }
}