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
public class Reviews {

    private final UUID id;
    private final UUID brandId; // FK на Brands.id
    private final String content;
    private final Integer rating; // от 1 до 5
    private final LocalDateTime createdAt;
    private final LocalDateTime indexedAt;
    private final String authorName;
    private final String source;
    private final Set<Categories> categories; // many-to-many сторона Reviews


    public void addCategory(Categories category) { this.categories.add(category); }
    public void removeCategory(Categories category) { this.categories.remove(category); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reviews that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Reviews.class.hashCode(); }

}