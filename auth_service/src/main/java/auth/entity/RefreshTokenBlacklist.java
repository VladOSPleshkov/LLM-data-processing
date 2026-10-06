package auth.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "refresh_token_blacklist")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class RefreshTokenBlacklist {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token")
    private String token;

    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoke_reason")
    private String revokeReason;

    @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (org.hibernate.Hibernate.getClass(this) != org.hibernate.Hibernate.getClass(o)) {
                return false;
            }
            RefreshTokenBlacklist that = (RefreshTokenBlacklist) o;
            if (getId() == null || that.getId() == null) {
                return false;
            }
            return Objects.equals(getId(), that.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getId());
        }
}