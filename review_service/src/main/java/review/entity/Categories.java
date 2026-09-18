package review.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Categories {

    private final UUID id;
    private final String name;
    private final LocalDateTime createdAt;
    private final Set<Reviews> reviews; // many-to-many сторона Categories

    public void addReview(Reviews review) { this.reviews.add(review); }
    public void removeReview(Reviews review) { this.reviews.remove(review); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categories that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Categories.class.hashCode(); }

}