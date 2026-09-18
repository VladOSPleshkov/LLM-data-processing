package auth.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenBlacklist {

    private final UUID id;
    private final String token;
    private final UUID userId;
    private final LocalDateTime blacklistedAt;
    private final LocalDateTime expiresAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefreshTokenBlacklist that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return RefreshTokenBlacklist.class.hashCode(); }

}