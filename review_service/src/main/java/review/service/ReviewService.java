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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * получение полной информации об отзыве (бренд + категории + вектор) выполняется ОДНИМ JPQL-запросом
 * через ReviewsDao#findByIdWithDetails / #findAllWithDetails, что устраняет
 * N+1, присутствовавший в исходной реализации (там категории и вектор
 * дотягивались отдельными DAO-вызовами в цикле для каждого отзыва).
 *
 * Управление ManyToMany выполняется через модификацию коллекции
 * Reviews.categories — JPA-провайдер сам синхронизирует join-таблицу
 * review_categories при флаше транзакции, отдельный ReviewCategoryDao
 * (как в legacy) больше не требуется.
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
                .orElseThrow(() -> new EntityNotFoundException("Бренд с ID " + request.brandId() + " не найден"));

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
    @Transactional(readOnly = true)
    public ReviewFullResponse getReviewById(UUID id) {
        Reviews review = reviewsDao.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с ID " + id + " не найден"));
        return mapToFullResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
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
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с ID " + id + " не найден"));

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

        // existing управляема текущей транзакцией — merge не требуется (dirty checking).
        return mapToFullResponse(existing);
    }

    @Override
    @Transactional("reviewTransactionManager")
    public boolean deleteReview(UUID id) {
        return reviewsDao.findById(id)
                .map(review -> {
                    // vectorStore удалится каскадно (orphanRemoval=true на Reviews.vectorStore),
                    // join-строки review_categories удалит JPA-провайдер автоматически.
                    reviewsDao.delete(review);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional("reviewTransactionManager")
    public ReviewFullResponse addCategory(AddCategoryToReviewRequest request) {
        Reviews review = reviewsDao.findByIdWithDetails(request.reviewId())
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с ID " + request.reviewId() + " не найден"));
        Categories category = categoriesDao.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Категория с ID " + request.categoryId() + " не найдена"));

        review.addCategory(category);
        return mapToFullResponse(review);
    }

    @Override
    @Transactional("reviewTransactionManager")
    public ReviewFullResponse removeCategory(RemoveCategoryFromReviewRequest request) {
        Reviews review = reviewsDao.findByIdWithDetails(request.reviewId())
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с ID " + request.reviewId() + " не найден"));
        Categories category = categoriesDao.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Категория с ID " + request.categoryId() + " не найдена"));

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

    /**
     * Маппинг в DTO без единого дополнительного SQL-запроса:
     * review.getBrand() / getCategories() / getVectorStore() уже загружены
     * JOIN FETCH-запросом в ReviewsDao и находятся в persistence context.
     */
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