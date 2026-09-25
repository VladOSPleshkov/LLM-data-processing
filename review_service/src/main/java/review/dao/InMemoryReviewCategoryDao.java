package review.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository("reviewCategoryDao")
public class InMemoryReviewCategoryDao implements IReviewCategoryDao {

    private record Link(UUID reviewId, UUID categoryId) {}

    private final Set<Link> links = ConcurrentHashMap.newKeySet();

    @Override
    public void link(UUID reviewId, UUID categoryId) { links.add(new Link(reviewId, categoryId)); }

    @Override
    public void unlink(UUID reviewId, UUID categoryId) { links.remove(new Link(reviewId, categoryId)); }

    @Override
    public List<UUID> findCategoryIdsByReviewId(UUID reviewId) {
        return links.stream().filter(l -> l.reviewId().equals(reviewId))
                .map(Link::categoryId).collect(Collectors.toList());
    }

    @Override
    public List<UUID> findReviewIdsByCategoryId(UUID categoryId) {
        return links.stream().filter(l -> l.categoryId().equals(categoryId))
                .map(Link::reviewId).collect(Collectors.toList());
    }

    @Override
    public void deleteAllByReviewId(UUID reviewId) { links.removeIf(l -> l.reviewId().equals(reviewId)); }

    @Override
    public void deleteAllByCategoryId(UUID categoryId) { links.removeIf(l -> l.categoryId().equals(categoryId)); }
}