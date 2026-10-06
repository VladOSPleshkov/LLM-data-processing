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

import com.shared.dto.review.*;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IReviewService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import review.dao.BrandsDao;
import review.dao.CategoriesDao;
import review.dao.ReviewsDao;
import review.dao.VectorStoreDao;
import review.entity.Brands;
import review.entity.Categories;
import review.entity.Reviews;
import review.entity.VectorStore;
import review.exception.ReviewNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * получение полной информации об отзыве (бренд + категории + вектор) выполняется JPQL-запросом
 * через ReviewsDao#findByIdWithDetails / #findAllWithDetails,
 * Управление ManyToMany выполняется через модификацию коллекции
 * Reviews.categories — JPA-провайдер сам синхронизирует join-таблицу
 * review_categories при флаше транзакции
 */
@Component("reviewService")
public class ReviewService implements IReviewService {

    private final ReviewsDao reviewsDao;
    private final BrandsDao brandsDao;
    private final CategoriesDao categoriesDao;
    private final VectorStoreDao vectorStoreDao;

    public ReviewService(ReviewsDao reviewsDao, BrandsDao brandsDao,
                         CategoriesDao categoriesDao, VectorStoreDao vectorStoreDao) {
        this.reviewsDao = reviewsDao;
        this.brandsDao = brandsDao;
        this.categoriesDao = categoriesDao;
        this.vectorStoreDao = vectorStoreDao;
    }

    @Override
    @Transactional("reviewTransactionManager")
    public ReviewFullResponse createReview(CreateReviewRequest request) {
        Brands brand = brandsDao.findById(request.brandId())
                .orElseThrow(() -> new ReviewNotFoundException(request.brandId()));

        Reviews entity = Reviews.builder()
                .brand(brand)
                .content(request.content())
                .rating(request.rating())
                .authorName(request.authorName())
                .source(request.source())
                .createdAt(LocalDateTime.now())
                .indexedAt(LocalDateTime.now())
                .build();

        List<String> categoryNames = request.categoryNames() == null ? List.of() : request.categoryNames();
        for (String categoryName : categoryNames) {
            Categories category = findOrCreateCategory(categoryName);
            entity.addCategory(category);
        }

        Reviews savedReview = reviewsDao.save(entity);

        if (request.embedding() != null) {
            VectorStore vectorStore = VectorStore.builder()
                    .embedding(request.embedding())
                    .metadata(request.metadata())
                    .build();
            savedReview.assignVectorStore(vectorStore);
            vectorStoreDao.save(vectorStore);
        }

        return mapToFullResponse(savedReview);
    }

    @Override
    @Transactional(value = "reviewTransactionManager", readOnly = true)
    public ReviewFullResponse getReviewById(UUID id) {
        Reviews review = reviewsDao.findByIdWithDetails(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        return mapToFullResponse(review);
    }

    @Override
    @Transactional(value = "reviewTransactionManager", readOnly = true)
    public List<ReviewFullResponse> getAllReviews() {
        List<ReviewFullResponse> responses = new ArrayList<>();
        for (Reviews review : reviewsDao.findAllWithDetails()) {
            responses.add(mapToFullResponse(review));
        }
        return responses;
    }

    @Override
    @Transactional("reviewTransactionManager")
    public ReviewFullResponse updateReview(UUID id, UpdateReviewRequest request) {
        Reviews existing = reviewsDao.findByIdWithDetails(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));

        if (request.content() != null) {
            existing.setContent(request.content());
        }
        if (request.rating() != null) {
            existing.setRating(request.rating());
        }
        existing.setIndexedAt(LocalDateTime.now());

        if (request.categoryNames() != null) {
            existing.clearCategories();
            for (String categoryName : request.categoryNames()) {
                Categories category = findOrCreateCategory(categoryName);
                existing.addCategory(category);
            }
        }
        return mapToFullResponse(existing);
    }

    @Override
    @Transactional("reviewTransactionManager")
    public boolean deleteReview(UUID id) {
        return reviewsDao.findById(id)
                .map(review -> {
                    reviewsDao.delete(review);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional("reviewTransactionManager")
    public ReviewFullResponse addCategory(AddCategoryToReviewRequest request) {
        Reviews review = reviewsDao.findByIdWithDetails(request.reviewId())
                .orElseThrow(() -> new ReviewNotFoundException(request.reviewId()));
        Categories category = categoriesDao.findById(request.categoryId())
                .orElseThrow(() -> new ReviewNotFoundException(request.categoryId()));

        review.addCategory(category);
        return mapToFullResponse(review);
    }

    @Override
    @Transactional("reviewTransactionManager")
    public ReviewFullResponse removeCategory(RemoveCategoryFromReviewRequest request) {
        Reviews review = reviewsDao.findByIdWithDetails(request.reviewId())
                .orElseThrow(() -> new ReviewNotFoundException(request.reviewId()));
        Categories category = categoriesDao.findById(request.categoryId())
                .orElseThrow(() -> new ReviewNotFoundException(review.getId()));

        review.removeCategory(category);
        return mapToFullResponse(review);
    }

    private Categories findOrCreateCategory(String name) {
        return categoriesDao.findByName(name)
                .orElseGet(() -> categoriesDao.save(
                        Categories.builder()
                                .name(name)
                                .createdAt(LocalDateTime.now())
                                .build()
                ));
    }

    private ReviewFullResponse mapToFullResponse(Reviews entity) {
        List<String> categoryNames = entity.getCategories().stream()
                .map(Categories::getName)
                .collect(Collectors.toList());

        float[] embedding = entity.getVectorStore() != null
                ? entity.getVectorStore().getEmbedding()
                : null;

        return new ReviewFullResponse(
                entity.getId(),
                entity.getBrand().getId(),
                entity.getBrand().getBrandName(),
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