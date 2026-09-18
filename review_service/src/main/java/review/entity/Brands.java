package review.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Brands {

    private final UUID id;
    private final String brandName;
    private final String description;
    private final LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brands brands)) return false;
        return id != null && Objects.equals(id, brands.id);
    }

    @Override
    public int hashCode() { return Brands.class.hashCode(); }

}