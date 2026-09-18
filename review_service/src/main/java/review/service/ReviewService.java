package review.service;

import com.shared.dto.review.*;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IReviewService;
import org.springframework.stereotype.Component;
import review.dao.*;
import review.entity.Categories;
import review.entity.Reviews;
import review.entity.VectorStore;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component("reviewService")
public class ReviewService implements IReviewService {

    private final IReviewDao reviewDao;
    private final IVectorStoreDao vectorStoreDao;
    private final ICategoriesDao categoriesDao;
    private final IBrandsDao brandsDao;
    private final IReviewCategoryDao reviewCategoryDao;

    public ReviewService(IReviewDao reviewDao, IVectorStoreDao vectorStoreDao,
                         ICategoriesDao categoriesDao, IBrandsDao brandsDao,
                         IReviewCategoryDao reviewCategoryDao) {
        this.reviewDao = reviewDao;
        this.vectorStoreDao = vectorStoreDao;
        this.categoriesDao = categoriesDao;
        this.brandsDao = brandsDao;
        this.reviewCategoryDao = reviewCategoryDao;
    }

    @Override
    public ReviewFullResponse createReview(CreateReviewRequest request) {
        Reviews entity = Reviews.builder()
                .brandId(request.brandId())
                .content(request.content())
                .rating(request.rating())
                .authorName(request.authorName())
                .source(request.source())
                .createdAt(LocalDateTime.now())
                .indexedAt(LocalDateTime.now())
                .build();

        Reviews savedReview = reviewDao.create(entity);
        UUID reviewId = savedReview.getId();

        // many-to-many: связываем с категориями (создаём при отсутствии)
        List<String> categoryNames = request.categoryNames() == null ? List.of() : request.categoryNames();
        for (String categoryName : categoryNames) {
            UUID categoryId = categoriesDao.findOrCreateCategory(categoryName);
            reviewCategoryDao.link(reviewId, categoryId);
        }

        // 1:1 c VectorStore
        if (request.embedding() != null) {
            vectorStoreDao.save(VectorStore.builder()
                    .reviewsId(reviewId)
                    .embedding(request.embedding())
                    .metadata(request.metadata())
                    .build());
        }

        return mapToFullResponse(savedReview);
    }

    @Override
    public ReviewFullResponse getReviewById(UUID id) {
        Reviews review = reviewDao.readById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с ID " + id + " не найден"));
        return mapToFullResponse(review);
    }

    @Override
    public List<ReviewFullResponse> getAllReviews() {
        List<ReviewFullResponse> responses = new ArrayList<>();
        for (Reviews review : reviewDao.readAll()) {
            responses.add(mapToFullResponse(review));
        }
        return responses;
    }

    @Override
    public ReviewFullResponse updateReview(UUID id, UpdateReviewRequest request) {
        Reviews existing = reviewDao.readById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с ID " + id + " не найден"));

        Reviews updatedEntity = Reviews.builder()
                .id(existing.getId())
                .brandId(existing.getBrandId())
                .content(request.content() != null ? request.content() : existing.getContent())
                .rating(request.rating() != null ? request.rating() : existing.getRating())
                .createdAt(existing.getCreatedAt())
                .indexedAt(LocalDateTime.now())
                .authorName(existing.getAuthorName())
                .source(existing.getSource())
                .build();

        reviewDao.update(updatedEntity);

        if (request.categoryNames() != null) {
            reviewCategoryDao.deleteAllByReviewId(id);
            for (String categoryName : request.categoryNames()) {
                UUID categoryId = categoriesDao.findOrCreateCategory(categoryName);
                reviewCategoryDao.link(id, categoryId);
            }
        }

        return getReviewById(id);
    }

    @Override
    public boolean deleteReview(UUID id) {
        vectorStoreDao.deleteByReviewId(id);
        reviewCategoryDao.deleteAllByReviewId(id);
        return reviewDao.deleteById(id);
    }

    @Override
    public ReviewFullResponse addCategory(AddCategoryToReviewRequest request) {
        reviewDao.readById(request.reviewId())
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с ID " + request.reviewId() + " не найден"));
        categoriesDao.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Категория с ID " + request.categoryId() + " не найдена"));

        reviewCategoryDao.link(request.reviewId(), request.categoryId());
        return getReviewById(request.reviewId());
    }

    @Override
    public ReviewFullResponse removeCategory(RemoveCategoryFromReviewRequest request) {
        reviewCategoryDao.unlink(request.reviewId(), request.categoryId());
        return getReviewById(request.reviewId());
    }

    private ReviewFullResponse mapToFullResponse(Reviews entity) {
        String brandName = brandsDao.findById(entity.getBrandId())
                .map(review.entity.Brands::getBrandName)
                .orElse("UNKNOWN");

        List<String> categoryNames = reviewCategoryDao.findCategoryIdsByReviewId(entity.getId()).stream()
                .map(catId -> categoriesDao.findById(catId).map(Categories::getName).orElse("UNKNOWN"))
                .collect(Collectors.toList());

        float[] embedding = vectorStoreDao.findByReviewId(entity.getId())
                .map(VectorStore::getEmbedding)
                .orElse(null);

        return new ReviewFullResponse(
                entity.getId(),
                entity.getBrandId(),
                brandName,
                entity.getContent(),
                entity.getRating(),
                entity.getCreatedAt(),
                entity.getIndexedAt(),
                entity.getAuthorName(),
                entity.getSource(),
                categoryNames,
                embedding
        );
    }
}