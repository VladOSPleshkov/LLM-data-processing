package auth.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedEntityGraph( name = "User.withRoles",
        attributeNodes = @NamedAttributeNode(value = "userRoles", subgraph = "userRoles-role"),
        subgraphs = @NamedSubgraph( name = "userRoles-role", attributeNodes = @NamedAttributeNode("role") ) )
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Column(nullable = false)
    private boolean enabled;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRoles> userRoles = new HashSet<>();

    public void addRole(Roles role, UUID assignedBy) {
        boolean alreadyExists = this.userRoles.stream()
                .anyMatch(ur -> ur.getRole().getId().equals(role.getId()));
        if (!alreadyExists) {
            UserRoles userRole = UserRoles.of(this, role, assignedBy);
            this.userRoles.add(userRole);
            role.getUserRoles().add(userRole);
        }
    }

    public void removeRole(Roles role) {
        this.userRoles.removeIf(ur -> Objects.equals(ur.getRole().getId(), role.getId()));
        role.getUserRoles().removeIf(ur -> Objects.equals(ur.getUser().getId(), this.getId()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return getEmail() != null && Objects.equals(getEmail(), that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }
}