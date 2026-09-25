package review.service;

import com.shared.dto.review.CreateVectorRequest;
import com.shared.dto.review.UpdateVectorRequest;
import com.shared.dto.review.VectorStoreDto;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IVectorStoreService;
import org.springframework.stereotype.Component;
import review.dao.IVectorStoreDao;
import review.entity.VectorStore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("vectorStoreService")
public class VectorStoreService implements IVectorStoreService {

    private final IVectorStoreDao vectorStoreDao;

    public VectorStoreService(IVectorStoreDao vectorStoreDao) {
        this.vectorStoreDao = vectorStoreDao;
    }

    @Override
    public VectorStoreDto createVector(CreateVectorRequest request) {
        VectorStore entity = VectorStore.builder()
                .reviewsId(request.reviewsId())
                .embedding(request.embedding())
                .metadata(request.metadata())
                .build();
        return mapToDto(vectorStoreDao.save(entity));
    }

    @Override
    public VectorStoreDto getVectorById(UUID id) {
        return mapToDto(vectorStoreDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вектор с ID " + id + " не найден")));
    }

    @Override
    public List<VectorStoreDto> getAllVectors() {
        List<VectorStoreDto> result = new ArrayList<>();
        for (VectorStore v : vectorStoreDao.findAll()) result.add(mapToDto(v));
        return result;
    }

    @Override
    public VectorStoreDto updateVector(UUID id, UpdateVectorRequest request) {
        VectorStore existing = vectorStoreDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вектор с ID " + id + " не найден"));

        VectorStore updated = VectorStore.builder()
                .id(existing.getId())
                .reviewsId(existing.getReviewsId())
                .embedding(request.embedding() != null ? request.embedding() : existing.getEmbedding())
                .metadata(request.metadata() != null ? request.metadata() : existing.getMetadata())
                .build();

        vectorStoreDao.update(updated);
        return mapToDto(updated);
    }

    @Override
    public boolean deleteVector(UUID id) { return vectorStoreDao.deleteById(id); }

    private VectorStoreDto mapToDto(VectorStore v) {
        return new VectorStoreDto(v.getId(), v.getReviewsId(), v.getEmbedding(), v.getMetadata());
    }
}