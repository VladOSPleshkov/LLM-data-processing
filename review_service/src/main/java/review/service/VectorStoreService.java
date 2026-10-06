package review.service;

import com.shared.dto.review.CreateVectorRequest;
import com.shared.dto.review.UpdateVectorRequest;
import com.shared.dto.review.VectorStoreDto;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IVectorStoreService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import review.dao.ReviewsDao;
import review.dao.VectorStoreDao;
import review.entity.Reviews;
import review.entity.VectorStore;
import review.exception.VectorStoreNotFoundException;

import java.util.UUID;

@Component("vectorStoreService")
public class VectorStoreService implements IVectorStoreService {

    private final VectorStoreDao vectorStoreDao;
    private final ReviewsDao reviewsDao;

    public VectorStoreService(VectorStoreDao vectorStoreDao, ReviewsDao reviewsDao) {
        this.vectorStoreDao = vectorStoreDao;
        this.reviewsDao = reviewsDao;
    }

    @Override
    @Transactional("reviewTransactionManager")
    public VectorStoreDto createVector(CreateVectorRequest request) {
        Reviews review = reviewsDao.findById(request.reviewsId())
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с ID " + request.reviewsId() + " не найден"));

        VectorStore entity = VectorStore.builder()
                .embedding(request.embedding())
                .metadata(request.metadata())
                .build();
        review.assignVectorStore(entity);

        return mapToDto(vectorStoreDao.save(entity));
    }

    @Override
    @Transactional(value = "reviewTransactionManager", readOnly = true)
    public VectorStoreDto getVectorById(UUID id) {
        return mapToDto(vectorStoreDao.findById(id)
                .orElseThrow(() -> new VectorStoreNotFoundException(id)));
    }


    @Override
    @Transactional(value = "reviewTransactionManager", readOnly = true)
    public VectorStoreDto getByReviewId(UUID reviewId) {
        return vectorStoreDao.findByReviewId(reviewId)
                .map(this::mapToDto)
                .orElseThrow(() -> VectorStoreNotFoundException.byReviewId(reviewId));
    }
    @Override
    @Transactional("reviewTransactionManager")
    public VectorStoreDto updateVector(UUID id, UpdateVectorRequest request) {
        VectorStore existing = vectorStoreDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вектор с ID " + id + " не найден"));

        if (request.embedding() != null) {
            existing.setEmbedding(request.embedding());
        }
        if (request.metadata() != null) {
            existing.setMetadata(request.metadata());
        }
        return mapToDto(existing);
    }

    @Override
    @Transactional("reviewTransactionManager")
    public boolean deleteVector(UUID id) {
        if (vectorStoreDao.findById(id).isEmpty()) {
            return false;
        }
        vectorStoreDao.deleteById(id);
        return true;
    }

    private VectorStoreDto mapToDto(VectorStore v) {
        UUID reviewId = v.getReview() != null ? v.getReview().getId() : null;
        return new VectorStoreDto(v.getId(), reviewId, v.getEmbedding(), v.getMetadata());
    }
}