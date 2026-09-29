package review.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Convert;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "vector_store")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VectorStore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Convert(converter = FloatArrayConverter.class)
    @Column(name = "embedding", columnDefinition = "TEXT")
    private float[] embedding;

    /** Владеющая сторона OneToOne: FK review_id уникален -> 1:1. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false, unique = true)
    private Reviews review;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VectorStore that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}