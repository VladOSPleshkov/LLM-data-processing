package review.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VectorStore {

    private final UUID id;
    private final String metadata;
    private final float[] embedding;
    private final UUID reviewsId; // FK 1:1 на Reviews.id

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VectorStore that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return VectorStore.class.hashCode(); }

}